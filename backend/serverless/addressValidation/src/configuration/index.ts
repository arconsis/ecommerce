import dotenv from 'dotenv';

dotenv.config();

export default {
    shippo: {
        basePath: "https://api.goshippo.com",
        apiKey: process.env.SHIPPO_API_TOKEN
    },
};