package com.deepthought.services.rest

import com.deepthought.services.impl.FakeMailer
import groovyx.net.http.RESTClient
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [Application.class])
@ActiveProfiles("test")
abstract class SpringBootTestBase {


    @LocalServerPort
    int randomServerPort

    String contextPath = '/external/auth/v1'

    @Value('${app.admin.email}')
    String adminEmail

    @Autowired
    FakeMailer fakeMailer

    RESTClient restClient

    @BeforeClass
    static void init() {

    }

    @Before
    void setup() {
        if (restClient == null) {
            restClient = new RESTClient("http://localhost:" + randomServerPort, JSON)
            restClient.handler.failure = restClient.handler.success
        }
    }

    @After
    void clean() {
        fakeMailer.clean()
    }

    def doGet(String path, Map<String, ?> args = new HashMap<>()) {
        args.path = contextPath + path
        return restClient.get(args)
    }

    def doDelete(String path, Map<String, ?> args = new HashMap<>()) {
        args.path = contextPath + path
        return restClient.delete(args)
    }

    def doPost(String path, Map<String, ?> args = new HashMap<>()) {
        args.path = contextPath + path
        return restClient.post(args)
    }

    def doPut(String path, Map<String, ?> args = new HashMap<>()) {
        args.path = contextPath + path
        return restClient.put(args)
    }


    def register(user) {
        return doPost('/register', [
                body: [
                        "email"   : user.email.toString(),
                        "fullName": user.fullName.toString()
                ]
        ])
    }

    def registerAndLogin(user) {

        def resp = register(user)

        assert resp.status == 201
        assert fakeMailer.lastMailSentUser.email == user.email

        return resetPasswordAndLogin(user)
    }

    def resetPasswordAndLogin(user) {
        doPost("/forgot-password", [
                body: [
                        email: user.email
                ]
        ])

        def resp = doPost("/auth/create-password", [
                body: [
                        email      : user.email,
                        newPassword: user.password,
                        token      : fakeMailer.lastMailSentUserCredential.passwordResetToken
                ]
        ])
        assert resp.status == 201

        return login(user.email, user.password).data
    }

    def login(String email, String password) {
        return doPost("/login", [
                requestContentType: URLENC,
                body              : [
                        email   : email,
                        password: password
                ]])
    }

    def logout(String sessionToken) {
        return doGet("/logout", [
                headers: headersFor(sessionToken)
        ])
    }

    def sessionOf(String sessionToken) {
        return doGet("/session", [
                headers: headersFor(sessionToken)
        ])
    }

    def headersFor(String session) {
        return [
                "Accept" : "application/json",
                "Authorization": session
        ]
    }
}
