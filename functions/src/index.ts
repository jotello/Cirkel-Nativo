const functions = require('firebase-functions');
const admin = require("firebase-admin");
admin.initializeApp(functions.config().firebase);

const geolib = require("geolib");
const nodemailer = require("nodemailer");
const cors = require('cors')({origin: true});
const twilio = require('twilio');
const accountSid = functions.config().twilio.sid;
const authToken = functions.config().twilio.token;
const client = new twilio(accountSid, authToken);
const twilioNumber = '+12513134964';

const transporter = nodemailer.createTransport({
  service: 'gmail',
  auth: {
      user: 'contacto.cirkel@gmail.com',
      pass: 'julziyaimxjouipc'
  }
});

exports.sendPushNotification = functions.database
  .ref("users/{mainUserId}/alerts/{alertId}")
  .onCreate(async (change: any, context: { params: { mainUserId: any; alertId: any; }; }) => {
  
    const mainUserId = context.params.mainUserId;
    const alertId = context.params.alertId;
    const refUser = await admin.database().ref("users/" + mainUserId).once("value");
    const mainUserName = refUser.child("name").val();
    const alert = refUser.child("alerts/"+ alertId).val();
    const locationAddress = alert.address;
    const locationLatitude = alert.location.latitude;
    const locationLongitude = alert.location.longitude;
    const payload = {
      notification: {
        title: "Alerta",
        body: mainUserName+ " está en peligro. Se encuentra actualmente en " + locationAddress
      }
    };
    const userContacts = refUser.child("contacts").val();

    if(userContacts !== null) {
      for (const key in userContacts) {
        const contactPhoneNumber = key;
        const contact = userContacts[key];
        // PUSH NOTIFICATION
        const refContact = admin.database().ref("users/");
        refContact.orderByChild("phoneNumber").equalTo(contactPhoneNumber).once("value", 
          function (userSnapshot: any[]) {
            userSnapshot.forEach((element: any) => {
              const tokenContact = element.val().fcmToken;
              if (tokenContact !== null && tokenContact !== '') {
                admin.messaging().sendToDevice(tokenContact, payload);
              }
            });
        });
        // SMS
        const textMessage = {
          body: mainUserName + ' está en peligro. Su ubicación es: https://www.google.com/maps/search/?api=1&query=' 
          + locationLatitude + ','+ locationLongitude,
          to: contactPhoneNumber,
          from: twilioNumber
        };
        // MAIL 
        const dest = contact.email;
        const mailOptions = {
            from: 'Cirkel <contacto.cirkel@gmail.com>', // Something like: Jane Doe <janedoe@gmail.com>
            to: dest,
            subject: 'Cirkel: ' + mainUserName + ' esta en peligro!', // email subject
            html: '<p style="font-size: 16px;">Tu contacto <b>' + mainUserName +
                  '</b> ha generado una alerta de peligro en: https://www.google.com/maps/search/?api=1&query='
                  + locationLatitude +','+locationLongitude+'</p>' // email content in HTML
        };
        return transporter.sendMail(mailOptions, (error: any, info: any) => {
          client.messages.create(textMessage, (errorMessage: any, infoMessage: any) => {
            //TO DO: Actualizar el plan para poder enviar sms's
            console.log("error", errorMessage);
            console.log("info", infoMessage);
          });
          //console.log("error", error);
          //console.log("info", info);
        });
      }
    }
    return true;
});

