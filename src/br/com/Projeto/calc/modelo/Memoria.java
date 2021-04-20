package br.com.Projeto.calc.modelo;

import java.util.ArrayList;
import java.util.List;

public class Memoria {
	
	private static final Memoria instancia = new Memoria();
	
	private final List<MemoriaObservador> observadores = new ArrayList<>();
	
	private TiposDeComandos ultimaOperacao = null;
	private boolean substituir = false;
	private String textoAtual = "";
	private String textoBuffer = "";
	
	private Memoria() {
	
	}
	
	public static Memoria getInstancia() {
		return instancia;
	}
	
	public void adicionarObservador(MemoriaObservador o) {
		observadores.add(o);
	}
	
	public String getTextoAtual() {
		return textoAtual.isEmpty() ? "0" : textoAtual;
	}
	
	public void processarComando(String texto) {
		
		TiposDeComandos tipoComando = detectarTiposComandos(texto);
		System.out.println(tipoComando);
		
		if (tipoComando == null) {
			return;
		}else if (tipoComando == TiposDeComandos.ZERAR) {
			textoAtual = "";
			textoBuffer = "";
			substituir = false;
			ultimaOperacao = null;	
		}else if (tipoComando == TiposDeComandos.SINAL && textoAtual.contains("-")) {
			textoAtual = textoAtual.substring(1);
		}else if (tipoComando == TiposDeComandos.SINAL && !textoAtual.contains("-")) {
			textoAtual = "-" + textoAtual;
		}else if (tipoComando == TiposDeComandos.NUMERO || tipoComando == TiposDeComandos.VIRGULA) {
			textoAtual = substituir ? texto : textoAtual + texto;
			substituir = false;
		}else {
			substituir = true;
			textoAtual = ObterOperacao();
			textoBuffer = textoAtual;
			ultimaOperacao = tipoComando;
		}
		
		observadores.forEach(o -> o.valorAlterado(getTextoAtual()));
	}

	private String ObterOperacao() {
		if (ultimaOperacao == null || ultimaOperacao == TiposDeComandos.IGUAL ) {
			return textoAtual;
		}
		
		double numeroBuffer = Double.parseDouble(textoBuffer.replace(",", "."));
		double numeroAtual = Double.parseDouble(textoAtual.replace(",", "."));
		
		double Resultado = 0;
		
		if (ultimaOperacao == TiposDeComandos.SOMAR) {
			Resultado = numeroBuffer + numeroAtual;
		}else if (ultimaOperacao == TiposDeComandos.SUBTRAIR) {
			Resultado = numeroBuffer - numeroAtual;
		}else if (ultimaOperacao == TiposDeComandos.MULTIPLICAR) {
			Resultado = numeroBuffer * numeroAtual;
		}else if (ultimaOperacao == TiposDeComandos.DIVIDIR) {
			Resultado = numeroBuffer / numeroAtual;
		}
		
		String texto = Double.toString(Resultado).replace(".", ",");
		boolean inteiro = texto.endsWith(",0");	
		return inteiro ? texto.replace(",0", ""): texto;
	}

	private TiposDeComandos detectarTiposComandos(String texto) {
		if (textoAtual.isEmpty() && texto == "0") {
			return null;
		}
		
		try {
			Integer.parseInt(texto);
			return TiposDeComandos.NUMERO;
		} catch (NumberFormatException e) {
			// quando nao for numero vamos fazer os outros teste aqui
			
			if ("AC".equals(texto)) {
				return TiposDeComandos.ZERAR;
			}else if ("/".equals(texto)) {
				return TiposDeComandos.DIVIDIR;
			}else if ("+".equals(texto)) {
				return TiposDeComandos.SOMAR;
			}else if ("*".equals(texto)) {
				return TiposDeComandos.MULTIPLICAR;
			}else if ("=".equals(texto)) {
				return TiposDeComandos.IGUAL;
			}else if ("-".equals(texto)) {
				return TiposDeComandos.SUBTRAIR;
			}else if ("±".equals(texto)) {
				return TiposDeComandos.SINAL;
			}else if (",".equals(texto) && !textoAtual.contains(",")) {
				return TiposDeComandos.VIRGULA;
			}
		 }
		 return null;
	}	
}
