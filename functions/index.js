/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.sendFcmNotification = functions.https.onRequest(async (req, res) => {
  // Allow only POST
  if (req.method !== "POST") {
    return res.status(405).send("Method Not Allowed");
  }
  const {token, title, body} = req.body;
  if (!token || !title || !body) {
    return res.status(400).json({
      error: "token, title, and body are required",
    });
  }

  const message = {
    notification: {title, body},
    token,
  };

  try {
    const response = await admin.messaging().send(message);
    return res.status(200).json({
      success: true,
      response,
    });
  } catch (error) {
    return res.status(500).json({error: error.message});
  }
});

const {onDocumentWritten} = require("firebase-functions/v2/firestore");

exports.sendFcmToShareCodeWatchers = onDocumentWritten(
    {document: "schedules/{scheduleId}"},
    async (event) => {
      // Debug log to ensure function is triggered
      console.log(
          "FCM triggered for scheduleId:",
          event.params.scheduleId,
      );
      const schedule = event.data.after.data();
      if (!schedule || !schedule.sharedCode) return null;

      const shareCode = schedule.sharedCode;
      const tokensSnapshot = await admin
          .firestore()
          .collection("shareCodeWatchers")
          .doc(shareCode)
          .collection("tokens")
          .get();

      const tokens = [];
      tokensSnapshot.forEach((doc) => {
        const data = doc.data();
        if (data.fcmToken) tokens.push(data.fcmToken);
      });

      if (tokens.length === 0) return null;

      const payload = {
        notification: {
          title: "일정이 변경되었습니다",
          body: `${schedule.title}`,
        },
      };

      // Log tokens and payload for debugging
      console.log("Sending FCM to tokens:", tokens, payload);
      return admin.messaging().sendToDevice(tokens, payload);
    });
