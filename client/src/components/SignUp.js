import React, { useState } from "react";
import { Auth } from "aws-amplify";
import FormElement from "./FormElement";

const SignUp = () => {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [username, setUsername] = useState("");
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [waitingForCode, setWaitingForCode] = useState(false);
    const [code, setCode] = useState("");

    const signUp = (e) => {
        e.preventDefault();

        Auth.signUp({
            username: email, password, attributes: {
                email,
                'custom:username': username,
                'custom:firstName': firstName,
                'custom:lastName': lastName
            }
        })
            .then((data) => {
                console.log(data);
                setWaitingForCode(true);
                setPassword("");
                setUsername("");
                setFirstName("");
                setLastName("");
            })
            .catch((err) => {
                console.log(err);
            });
    };

    const confirmSignUp = (e) => {
        e.preventDefault();

        Auth.confirmSignUp(email, code)
            .then((data) => {
                console.log(data);
                setWaitingForCode(false);
                setEmail("");
                setCode("");
            })
            .catch((err) => console.log(err));
    };

    const resendCode = () => {
        Auth.resendSignUp(email)
            .then(() => {
                console.log("code resent successfully");
            })
            .catch((e) => {
                console.log(e);
            });
    };

    return (
        <div className="form">
            <h3>Sign Up</h3>
            {!waitingForCode && (
                <form>
                    <FormElement label="Email" forId="sign-up-email">
                        <input
                            id="sign-up-email"
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="email"
                        />
                    </FormElement>
                    <FormElement label="Password" forId="sign-up-email">
                        <input
                            id="sign-up-password"
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="password"
                        />
                    </FormElement>
                    <FormElement label="Username" forId="sign-up-email">
                        <input
                            id="sign-up-username"
                            type="username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            placeholder="username"
                        />
                    </FormElement>
                    <FormElement label="FirstName" forId="sign-up-firstName">
                        <input
                            id="sign-up-firstName"
                            type="text"
                            value={firstName}
                            onChange={(e) => setFirstName(e.target.value)}
                            placeholder="firstName"
                        />
                    </FormElement>
                    <FormElement label="LastName" forId="sign-up-lastName">
                        <input
                            id="sign-up-lastName"
                            type="text"
                            value={lastName}
                            onChange={(e) => setLastName(e.target.value)}
                            placeholder="lastName"
                        />
                    </FormElement>

                    <button type="submit" onClick={signUp}>
                        Sign Up
                    </button>
                </form>
            )}
            {waitingForCode && (
                <form>
                    <FormElement label="Confirmation Code" forId="sign-up-code">
                        <input
                            id="sign-up-code"
                            type="text"
                            value={code}
                            onChange={(e) => setCode(e.target.value)}
                            placeholder="code"
                        />
                    </FormElement>
                    <button type="submit" onClick={confirmSignUp}>
                        Confirm Sign Up
                    </button>
                    <button type="button" onClick={resendCode}>
                        Resend code
                    </button>
                </form>
            )}
        </div>
    );
};

export default SignUp;