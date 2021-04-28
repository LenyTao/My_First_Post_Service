package com.PostService


import java.text.SimpleDateFormat
import java.util.{Date, Properties}
import javax.mail.{Authenticator, Message, PasswordAuthentication, Session, Transport}
import javax.mail.internet.{InternetAddress, MimeMessage}

object SenderOfLetters extends App {

  def sendMail(recipient: String, event: Event) = {

    /** 1. Создаём соеденение с почтовым сервисом */
    val properties = new Properties()
    properties.put("mail.smtp.auth", "true")
    properties.put("mail.smtp.starttls.enable", "true")
    properties.put("mail.smtp.host", "smtp.gmail.com")
    properties.put("mail.smtp.port", "587")

    val myAccountEmail = "xxxx@gmail.com"
    val password = "xxxxxxxx"

    val session = Session.getInstance(properties, new Authenticator() {
      override protected def getPasswordAuthentication: PasswordAuthentication = {
        new PasswordAuthentication(myAccountEmail, password)
      }
    })

    /** ------------------------------------- */

    /** 2. Создаём само сообщение */
    val message: Message = prepareMessage(session, myAccountEmail, recipient, event)

    /** ---------------------- */

    /** 3. Отправляем сообщение */
    Transport.send(message)

    /** ---------------------- */
    println("Message send successfully")
  }

  /** Создаём само сообщение */
  private def prepareMessage(session: Session, myAccountEmail: String, recipient: String, event: Event) = {
    //Объект
    val message = new MimeMessage(session)
    //От кого
    message.setFrom(new InternetAddress(myAccountEmail))
    //Тема
    message.setSubject("Application status from bank N")
    //HTML
    message.setContent(
      s"""
         |<h1> Dear Client </h1>
         |<h2> Email for user with id: ${event.externalClientId.split("-")(1)}  </h2>
         |<h2> Today ${new SimpleDateFormat().format(event.data)} your application has received the status: ${event.stage}  </h2>
         |<h2> Processing: ${
        if (event.status) {
          "completed"
        } else {
          "not completed"
        }
      }   </h2>
         |<h3> Thanks for choosing us ! </h3>
         |""".stripMargin, "text/html"
    )
    //    //Текст сообщения
    //    message.setText("Your Status, \n Test!")
    //Кому
    message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient))
    //Дата
    message.setSentDate(new Date())

    message
  }
}
