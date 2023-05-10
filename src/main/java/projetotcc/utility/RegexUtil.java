package projetotcc.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {
	
	
	public static boolean emailInvalido(String email) {		
		
		String emailRegex = "[a-zA-Z]{1}[a-zA-Z0-9_.]+@[a-zA-Z0-9]+.[a-zA-Z]{2,3}[.]{0,1}[a-zA-Z]+";
				
		Pattern pattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(email);		
		
		return !matcher.matches();
	}
	
	/**
	 * Permite uma senha que tenha de 8 a 20 caracteres, sendo que ao menos:
	 * 1 tem que ser maiuscula;
	 * 1 tem que ser um caractere especial;
	 * 1 tem que ser numero.
	 * @param senha
	 * @return
	 */
	public static boolean senhaInvalida(String senha) {
		String regrasRegex = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*\\W+)(?=^.{8,20}$).*$";
		Pattern pattern = Pattern.compile(regrasRegex);
		Matcher matcher = pattern.matcher(senha);
		
		return !matcher.matches();		
	}
	
	/**
	 * Permite um nome que comece sempre com letra maiuscula, tenha até 30 caracteres, podendo ter 
	 * - ou _ ou . sendo que não podem estar no inicio e no final.
	 * @param nomeExibicao
	 * @return
	 */	
	public static boolean nomeUsuarioInvalido(String nomeExibicao) {
		String nomeUsuarioRegex = "^[A-Z]{1}[a-zA-Z0-9-_.À-ú ]{1,28}[A-Za-z0-9]{1}$";
		Pattern pattern = Pattern.compile(nomeUsuarioRegex);
		Matcher matcher = pattern.matcher(nomeExibicao);
		
		return !matcher.matches();
	}
	
	/**
	 * Permite um nome de projeto que comece com letra maiuscula, tenha até 30 caracteres, 
	 * permite acentos e espaço.
	 * @param nomeProjeto
	 * @return
	 */	
	public static boolean nomeProjetoInvalido(String nomeProjeto) {
		String nomeProjetoRegex = "^[A-Z]{1}[a-zA-Z0-9À-ú ]{1,28}[A-Za-z0-9À-ú]{1}$";
		
		Pattern pattern = Pattern.compile(nomeProjetoRegex);
		Matcher matcher = pattern.matcher(nomeProjeto);
		
		return !matcher.matches();
	}
	
	/**
	 * Permite nomes de tag com a primeira letra maiuscula, até 20 caracteres,
	 * além de caracteres acentuados, números e espaço.
	 * @param nomeTag
	 * @return
	 */
	public static boolean nomeTagInvalida(String nomeTag) {
		String nomeTagRegex = "^[A-Z]{1}[a-zA-ZÀ-ú0-9 ]{1,18}[A-Za-z0-9À-ú]{1}$";
		
		Pattern pattern = Pattern.compile(nomeTagRegex);
		Matcher matcher = pattern.matcher(nomeTag);
		
		return !matcher.matches();
	}

}
