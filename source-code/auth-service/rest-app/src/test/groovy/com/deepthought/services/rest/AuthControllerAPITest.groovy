package com.deepthought.services.rest

import org.junit.Test
import org.springframework.beans.factory.annotation.Value

class AuthControllerAPITest extends SpringBootTestBase {

    @Value('${app.admin.email}')
    String adminEmail

    def user1 = [
            email   : "user1@example.com",
            fullName: "User I Junior",
            password: "u1pass"
    ]

    @Test
    void logInOutTest() {
        def naiveAdmin = ['ada@example.com', 'ada']
        // simple login
        def response = login(naiveAdmin[0], naiveAdmin[1])
        assert response.status == 200;
        assert response.data.token != null
        assert response.data.user != null
        assert response.data.user.email == naiveAdmin[0]
        assert response.data.user.id != null
        assert response.data.user.fullName != null
        assert response.data.user.role != null

        def session = sessionOf(response.data.token)
        assert session.status == 200 // logged in successfulluy

        // wrong password
        def err = login(naiveAdmin[0], 'wrong-password')
        assert err.status == 401
        assert err.data  == null

        // logout
        def logoutResponse = logout(response.data.token) ;
        assert logoutResponse.status == 200;

        // should really logout
        session = sessionOf(response.data.token)
        assert session.status == 401


        // double logout shouldn't fail
        logoutResponse = logout(response.data.token) ;
        assert logoutResponse.status == 200;

        // logout in any case should not emit not authorized status

        assert logout(null).status == 200
        assert logout('invalid token format').status == 200
    }


    @Test
    void changeAndRecoverPasswordTest() {
        def user = ['susan@example.com', 'susan']

        def sessionResponse = login(user[0], user[1]);
        assert sessionResponse.status == 200

        //
        // change password normal flow
        //
        def changePassResponse = doPost('/change-password', [
                headers: headersFor(sessionResponse.data.token),
                body: [
                        oldPassword: user[1],
                        newPassword: user[1].reverse()
                ]
        ])
        assert changePassResponse.status == 200
        // make sure password really changed by log in with new pass
        assert login(user[0], user[1].reverse()).status == 200

        //
        // change password fail flows
        //

        assert doPost('/change-password', [
                headers: headersFor(sessionResponse.data.token),
                body: [
                        oldPassword: user[1],
                        newPassword: user[1].reverse()
                ]
        ]).status == 403 // forbidden as password already changed

        assert doPost('/change-password', [
                body: [
                        oldPassword: user[1].reverse(),
                        newPassword: user[1]
                ]
        ]).status == 401 // unauthorized as session is not provided

        //
        // Recover password
        //
        assert doPost("/forgot-password", [
                body: [
                        email: user[0]
                ]
        ]).status == 201 // recovery email sent

        def recoveryToken = fakeMailer.lastMailSentUserCredential.passwordResetToken
        assert doPost("/create-password", [
                body: [
                        email      : user[0],
                        newPassword: user[1],
                        token      : recoveryToken
                ]
        ]).status == 201 // reset successfully

        // second attempt should fail as token must be invalidated
        assert doPost("/create-password", [
                body: [
                        email      : user[0],
                        newPassword: user[1],
                        token      : recoveryToken
                ]
        ]).status == 409 // conflict, token used already
    }

    @Test
    void adminToRestorePasswordThenLoginAndLogout() {

        def response = doPost("/forgot-password", [
                body: [
                        email: adminEmail
                ]
        ])

        assert response.status == 201

        response = doPost("/create-password", [
                body: [
                        email      : adminEmail,
                        newPassword: adminEmail.reverse(),
                        token      : fakeMailer.lastMailSentUserCredential.passwordResetToken
                ]
        ])

        assert response.status == 201

        response = login(adminEmail, adminEmail.reverse())

        assert response.status == 200

        def authHeaders = headersFor(response.data.token)

        assert doGet("/session").status == 401 // auth headers not supplied

        response = doGet("/session", [headers: authHeaders])
        assert response.status == 200
        assert response.data.user.email == adminEmail

        assert doGet("/logout").status == 200 // auth headers not supplied
        assert doGet("/logout", [headers: authHeaders]).status == 200
        // already logged out
        assert doGet("/logout", [headers: authHeaders]).status == 200

        // after logout token should be expired
        assert doGet("/session").status == 401 // auth headers not supplied
        assert doGet("/session", [headers: authHeaders]).status == 401
    }

    @Test
    void doubleRegistrationTest() {
        def resp = register([email: "aaaaaa@example.com", fullName: "Jane Doe"])
        assert resp.status == 201

        resp = register([email: "aaaaaa@example.com", fullName: "John Doe"])
        assert resp.status == 409
        assert resp.data.message == "UserAlreadyExistsException"
    }

    @Test
    void registrationTest() {

        def resp = register([
                        "email"   : user1.email,
                        "fullName": user1.fullName
                ])

        assert resp.status == 201
        assert fakeMailer.lastMailSentUser.email == user1.email
        resp = doPost("/create-password", [
                body: [
                        email      : user1.email,
                        newPassword: user1.password,
                        token      : fakeMailer.lastMailSentUserCredential.passwordResetToken
                ]
        ])
        assert resp.status == 201

        resp = login(user1.email, user1.password)
        assert resp.status == 200
        assert resp.data.user.email == user1.email
        assert resp.data.user.fullName == user1.fullName
        assert resp.data.user.id != null
        assert resp.data.token != null
        assert resp.data.user.role == "BUYER"

        def authHeaders = headersFor(resp.data.token)

        resp = doGet("/session", [headers: authHeaders])
        assert resp.status == 200
        assert resp.data.user.email == user1.email
        assert resp.data.user.role ==  "BUYER"

        assert doGet("/logout", [headers: authHeaders]).status == 200

        assert doGet("/session", [headers: authHeaders]).status == 401
    }

    @Test
    void forgotPasswordOnNonExistingUserShouldKeepSilence() {
        def response = doPost("/forgot-password", [
                body: [
                        email: "email-of-a-not-registered-user@gmail.com"
                ]
        ])

        assert response.status == 201
    }


}