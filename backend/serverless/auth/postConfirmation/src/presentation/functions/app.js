const axios = require('axios');
const {
  usersApi: usersApiConfig,
} = require('../../configuration');

exports.handler = async function lambdaHandler(event, context) {
  try {
    console.log(`Event: ${JSON.stringify(event, null, 2)}`);
    console.log(`Context: ${JSON.stringify(context, null, 2)}`);
    const { userAttributes } = event.request;
    console.log(`userAttributes: ${JSON.stringify(userAttributes, null, 2)}`);
    await axios({
      method: 'post',
      url: `${usersApiConfig.basePath}/users`,
      data: {
        email: userAttributes.email,
        sub: userAttributes.sub,
        firstName: userAttributes['custom:firstName'],
        username: userAttributes['custom:username'],
        lastName: userAttributes['custom:lastName'],
      },
    });
    console.log('Success');
    return context.done(null, event);
  } catch (error) {
    console.log('Error on storing user to our system', error);
    return context.done(error, event);
  }
};
