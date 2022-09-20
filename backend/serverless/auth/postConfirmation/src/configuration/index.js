require('dotenv').config();

const config = {
  usersApi: {
    basePath: process.env.USERS_API_URL,
  },
};

module.exports = config;