import dotenv from 'dotenv';

dotenv.config();

const config = {
    cognito: {
        "aws_project_region": process.env.REACT_APP_AWS_PROJECT_REGION,
        "aws_cognito_region": process.env.REACT_APP_AWS_COGNITO_REGION,
        "aws_user_pools_id": process.env.REACT_APP_AWS_USER_POOL_ID,
        "aws_user_pools_web_client_id": process.env.REACT_APP_AWS_USER_POOL_WEB_CLIENT_ID,
    }
};

export default config;
