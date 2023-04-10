package projetotcc.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {
	
	
	public static boolean emailValido(String email) {		
		
		String emailRegex = "[a-zA-Z]{1}[a-zA-Z0-9_.]+@[a-zA-Z0-9]+.[a-zA-Z]{2,3}[.]{0,1}[a-zA-Z]+";
				
		Pattern pattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(email);		
		
		return matcher.matches();
	}
	
	/**
	 * Permite uma senha que tenha de 8 a 20 caracteres, sendo que ao menos:
	 * 1 tem que ser maiuscula;
	 * 1 tem que ser um caractere especial;
	 * 1 tem que ser numero.
	 * @param senha
	 * @return
	 */
	public static boolean senhaValida(String senha) {
		String regrasRegex = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*\\W+)(?=^.{8,20}$).*$";
		Pattern pattern = Pattern.compile(regrasRegex);
		Matcher matcher = pattern.matcher(senha);
		
		return matcher.matches();		
	}
	
	/**
	 * Permite um nome que comece sempre com letra maiuscula, tenha até 30 caracteres, podendo ter - ou _ ou . sendo que
	 * não podem estar no inicio e no final.
	 * @param nomeExibicao
	 * @return
	 */	
	public static boolean nomeUsuarioValido(String nomeExibicao) {
		String nomeUsuarioRegex = "^[A-Z]{1}[a-zA-Z0-9-_.À-ú ]{1,28}[A-Za-z0-9]{1}$";
		Pattern pattern = Pattern.compile(nomeUsuarioRegex);
		Matcher matcher = pattern.matcher(nomeExibicao);
		
		return matcher.matches();
	}

}
