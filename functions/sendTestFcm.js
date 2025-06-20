// const admin = require("firebase-admin");
//
// // Initialize with application default credentials or service account
// admin.initializeApp({
//  credential: admin.credential.applicationDefault(),
// });
//
// const registrationToken =
//  "";
//
// const message = {
//  notification: {
//    title: "Test Notification",
//    body: "This is a test from Node.js script.",
//  },
//  token: registrationToken,
// };
//
// admin
//    .messaging()
//    .send(message)
//    .then((response) => {
//      console.log("Successfully sent message:", response);
//    })
//    .catch((error) => {
//      console.error("Error sending message:", error);
//    });
