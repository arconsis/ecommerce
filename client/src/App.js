import React, { useEffect } from 'react';
import './App.css';
import Amplify from 'aws-amplify'
import config from './config'
import SignUp from './components/SignUp';
import SignIn from './components/SignIn';

const App = () => {
    useEffect(() => {
        Amplify.configure(config.cognito);
    });
    return (
        <div className="App">
            <header className="App-header">
                <h1>Ecommerce</h1>
                <h2>Authentication for Create React App using AWS Cognito</h2>
            </header>
            <SignUp />
            <SignIn />
        </div>
    );
};

export default App;