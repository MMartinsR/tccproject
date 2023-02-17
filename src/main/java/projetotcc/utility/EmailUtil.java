package projetotcc.utility;

import java.util.Date;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtil {
	
	/**
	 * Método utilitário para enviar emails com autenticação SSL
	 * Autenticação: sim
	 * Porta para SSL: 465
	 */
	
	public static void enviarEmail(Session sessao, String paraEmail, String assunto, String corpo) {
		
		try {
			
			MimeMessage msg = new MimeMessage(sessao);
			
			// Define os cabeçalhos da mensagem
			msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
			msg.addHeader("format", "flowed");
			msg.addHeader("Content-Transfer-Encoding", "8bit");
			
			msg.setFrom(new InternetAddress("no_reply@example.com", "NoReply-JD"));
			
			msg.setReplyTo(InternetAddress.parse("no_reply@example.com", false));
			
			msg.setSubject(assunto, "UTF-8");
			
			msg.setText(corpo, "UTF-8");
			
			msg.setSentDate(new Date());
			
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(paraEmail, false));
			System.out.println("A mensagem está pronta");
			
			Transport.send(msg);
			
			System.out.println("Email enviado com sucesso!");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
