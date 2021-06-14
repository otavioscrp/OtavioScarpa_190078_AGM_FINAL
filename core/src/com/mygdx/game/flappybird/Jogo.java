package com.mygdx.game.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;


public class Jogo extends ApplicationAdapter {



	//Determinar texturas que serão armazenadas
	private Texture passaros;
	private Texture fundo;
	private Texture canoAlto;
	private Texture canoBaixo;
	private Texture GameOver;
	private Texture moedaPrata;
	private Texture moedaOuro;
	private Texture logo;

	// "Canvas"/Interface de reinicio e pontuação
	BitmapFont textPontuacao;
	BitmapFont textReiniciar;
	BitmapFont textMelhorPontuacao;

	private boolean passouCano = false;

	private Random random;

	//Variaveis de pontuação, gravidade e etc
	private int pontuacaoMaxima = 0;
	private int pontos = 0;
	private int gravidade = 0;
	private int estadojogo = 0;
	private int moedapravalor = 0;
	int valor = 1;

	//Respectivamente, dimensões dadas pelo dispositivo, variação de animações,
	//posicionamento dos canos e do pássaro e espaçamento.
	private float variacao = 0;
	private float larguradispositivo;
	private float alturadispositivo;
	private float posicaoInicialVerticalPassaro;
	private float posicaoCanoHorizontal;
	private float posicaoCanoVertical;
	private float espacoEntreCanos;
	private float posicaoHorizontalPassaro = 0;

	//variaveis que determinam posicionamento das moedas
	private float posicaoMoedaouro;
	private float posicaoMoedaPrata;
	private float posicaomoedavetical;

	private SpriteBatch batch;

	private ShapeRenderer shapeRenderer;
	//variação de colisão por formas geometricas primitivas
	private Circle circuloPassaro;
	private Rectangle retanguloCanoCima;
	private Rectangle retanguloBaixo;
	private Circle circuloMoedaOuro;
	private Circle circuloMoedaPrata;

	//Sons do sistema
	Sound somVoando;
	Sound somColisao;
	Sound somPontuacao;
	Sound somMoedas;

	Preferences preferencias;



	@Override
	public void create() {

		//Começar o jogo com os metodos de carregamento/renderização de objetos e texturas
		inicializarObjetos();
		inicializaTexturas();

	}

	@Override
	public void render() {

		//Respectivamente, métodos de verificação de estado, detecção de colisão e texturas, e contagem de pontos
		verificaEstadojogo();
		desenharTexturas();
		detectarColisao();
		validarPontos();

	}

	private void inicializarObjetos() {

		batch = new SpriteBatch();
		random = new Random();//random


		//determinar as dimensões usadas no dispositivo
		larguradispositivo = Gdx.graphics.getWidth();
		alturadispositivo = Gdx.graphics.getHeight();

		//posicionamento do passaro na metade da altura do dispositivo e no começo do eixo de largura
		posicaoInicialVerticalPassaro = alturadispositivo / 2;
		posicaoCanoHorizontal = larguradispositivo;

		espacoEntreCanos = 350;

		posicaoCanoHorizontal = larguradispositivo;//possição do cano sera a largura do dispositivo
		posicaoMoedaouro = larguradispositivo;
		posicaoMoedaPrata = larguradispositivo;


		//configuração de texto na interface do jogo(cor, tamanho, etc)
		textPontuacao = new BitmapFont();
		textPontuacao.setColor(com.badlogic.gdx.graphics.Color.WHITE);
		textPontuacao.getData().setScale(10);

		//caracteristicas do texto de reinício
		textReiniciar = new BitmapFont();
		textReiniciar.setColor(Color.RED);
		textReiniciar.getData().setScale(2);

		//caracteristicas de melhor pontuação
		textMelhorPontuacao = new BitmapFont();
		textMelhorPontuacao.setColor(Color.GREEN);
		textMelhorPontuacao.getData().setScale(3);


		//determinação de colisões
		shapeRenderer = new ShapeRenderer();
		circuloPassaro = new Circle();
		retanguloCanoCima = new Rectangle();
		retanguloBaixo = new Rectangle();
		circuloMoedaOuro = new Circle();
		circuloMoedaPrata = new Circle();

		//selecionando assets para o sistema de som
		somVoando = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
		somColisao = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));
		somPontuacao = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));
		somMoedas = Gdx.audio.newSound(Gdx.files.internal("Som_Moeda.wav"));


		preferencias = Gdx.app.getPreferences("flappybird");
		pontuacaoMaxima = preferencias.getInteger("pontuacaoMaxima", 0);

	}

	private void inicializaTexturas() {

		//selecionando assets para configurar as texturas
		fundo = new Texture("fundo.png");


		//criação de um array para as animações do pássaro (para o angry bird, no caso só será selecionada uma imagem)
		passaros = new Texture("azul.png");

		//selecionando assets para os canos
		canoAlto = new Texture("cano_topo_maior.png");
		canoBaixo = new Texture("cano_baixo_maior.png");

		//selecionando assets para as moedas
		moedaOuro = new Texture("MoedaOuro.png");
		moedaPrata = new Texture("MoedaPrata.png");

		GameOver = new Texture("game_over.png");
		logo = new Texture("logo.jpg");


	}

	private void detectarColisao() {

		//setando configurações de colisão do pássaro e dos canos de cima e de baixo
		circuloPassaro.set(50 + passaros.getWidth() / 2f,
				posicaoInicialVerticalPassaro + passaros.getHeight() / 2f, passaros.getWidth() / 2f);

		retanguloBaixo.set(posicaoCanoHorizontal,
				alturadispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical,
				canoBaixo.getWidth(), canoBaixo.getHeight());

		retanguloCanoCima.set(posicaoCanoHorizontal,
				alturadispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVertical, canoAlto.getWidth(),
				canoAlto.getHeight());

		circuloMoedaPrata.set(posicaoMoedaPrata, alturadispositivo /2 + posicaomoedavetical + moedaPrata.getHeight() / 2f,
				moedaPrata.getWidth() / 2f);

		circuloMoedaOuro.set(posicaoMoedaouro, alturadispositivo /2 + posicaomoedavetical + moedaOuro.getHeight() / 2f,
				moedaOuro.getWidth() / 2f);


		//verificar colisão das moedas e dos canos

		boolean beteumoedaOuro = Intersector.overlaps(circuloPassaro, circuloMoedaOuro);
		boolean beteumoedaPrata = Intersector.overlaps(circuloPassaro, circuloMoedaPrata);

		boolean bateuCanoCima = Intersector.overlaps(circuloPassaro, retanguloCanoCima);
		boolean bateuCanoBaixo = Intersector.overlaps(circuloPassaro, retanguloBaixo);

		//se colidiu, acrescentar pontuação e som
		if (bateuCanoBaixo || bateuCanoCima) {

			if (estadojogo == 1)
			{

				somColisao.play();
				estadojogo = 2;

			}
		}
		if (beteumoedaOuro) {
			if (estadojogo == 1) {
				pontos += 10;
				moedapravalor = 0;
				somMoedas.play();
				posicaoMoedaouro = larguradispositivo;
			}
		}
		if (beteumoedaPrata) {

			if (estadojogo == 1) {
				pontos += 5;
				moedapravalor++;
				somMoedas.play();
				posicaoMoedaPrata = larguradispositivo;
			}
		}
	}

	private void validarPontos() {
		if (posicaoCanoHorizontal < 50 - passaros.getWidth()) {// se passar pelo cano
			if (!passouCano) {// for diferente passouCano
				pontos++; // soma pontos
				passouCano = true;// e passouCano é verdadeiro
				somPontuacao.play();// dispara som de pontuação


			}

		}

		variacao += Gdx.graphics.getDeltaTime() * 10;// velocidade da variação de pngs do passaro para a animação

		if (variacao > 3) // variação para animação do passaro
		{
			variacao = 0; // determinado que sera 0
		}
	}

	private void verificaEstadojogo() {

		//verificação de toque de tela do jogador
		boolean toqueTela = Gdx.input.justTouched();

		//ação de pulo do pássaro
		if (estadojogo == 0) {
			if (Gdx.input.justTouched()) {
				gravidade = -15;
				estadojogo = 1;
				somVoando.play();
			}

		} else if (estadojogo == 1) {
			valor = 0;

			if (toqueTela) {
				gravidade = -15;
				somVoando.play();
			}
			//posicionamento e movimentação dos canos e das moedas
			posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 200;
			if (posicaoCanoHorizontal < -canoBaixo.getWidth()) {
				posicaoCanoHorizontal = larguradispositivo;
				posicaoCanoVertical = random.nextInt(400) - 200;

				passouCano = false;
			}
			posicaoMoedaPrata -= Gdx.graphics.getDeltaTime() * 200;
			if (posicaoMoedaPrata < -moedaPrata.getWidth()) {
				posicaoMoedaPrata = larguradispositivo;
				posicaomoedavetical = random.nextInt(400) - 200;

			}
			if (moedapravalor >= 5) {
				posicaoMoedaouro -= Gdx.graphics.getDeltaTime() * 200;
				if (posicaoMoedaouro < -moedaOuro.getWidth()) {
					posicaoMoedaouro = larguradispositivo;
					posicaomoedavetical = random.nextInt(400) - 200;
					moedapravalor = 0;
				}
			}

			if (posicaoInicialVerticalPassaro > 0 || toqueTela)
				posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;


			//aumento constante de gravidade
			gravidade++;


		}
		//ação determinada ao colidir com o cano, ou seja, determinada a pontuação do jogador
		else if (estadojogo == 2) {
			if (pontos > pontuacaoMaxima)
			{
				pontuacaoMaxima = pontos;
				preferencias.putInteger("pontuacaoMaxima", pontuacaoMaxima);
			}

			posicaoHorizontalPassaro -= Gdx.graphics.getDeltaTime() * 500;

			//resetar dados do jogo ao reiniciar
			if (toqueTela)
			{
				estadojogo = 0;
				pontos = 0;
				gravidade = 0;
				posicaoHorizontalPassaro = 0;
				posicaoInicialVerticalPassaro = alturadispositivo / 2;
				posicaoCanoHorizontal = larguradispositivo;

				posicaoMoedaouro = larguradispositivo;
				posicaoMoedaPrata = larguradispositivo;
				moedapravalor = 0;
			}
		}


	}

	private void desenharTexturas() {
		batch.begin();

		batch.draw(fundo, 0, 0, larguradispositivo, alturadispositivo);

		batch.draw(passaros, 50 + posicaoHorizontalPassaro, posicaoInicialVerticalPassaro);
		batch.draw(canoBaixo, posicaoCanoHorizontal, alturadispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical);
		batch.draw(canoAlto, posicaoCanoHorizontal, alturadispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVertical);
		textPontuacao.draw(batch, String.valueOf(pontos), larguradispositivo / 2, alturadispositivo - 100);

		//interface principal do jogo
		if (estadojogo == 2) {
			batch.draw(GameOver, larguradispositivo / 2 - GameOver.getWidth() / 2f, alturadispositivo / 2);
			textReiniciar.draw(batch, "Toque  na tela para reiniciar!", larguradispositivo / 2 - 200, alturadispositivo / 2 - GameOver.getHeight() / 2f);
			textMelhorPontuacao.draw(batch, "Sua melhor pontuação  é : " + pontuacaoMaxima + " Pontos", larguradispositivo / 2 - 300, alturadispositivo / 2 - GameOver.getHeight() * 2);
		}
		if (estadojogo == 0 && valor == 1 )
		{
			batch.draw(logo, 0, 0, larguradispositivo, alturadispositivo);

		}
		if (moedapravalor <= 5) {

			batch.draw(moedaPrata, posicaoMoedaPrata, alturadispositivo /2 + posicaomoedavetical + moedaPrata.getHeight() / 2f);
		}

		if (moedapravalor >= 5) {

			batch.draw(moedaOuro, posicaoMoedaouro, alturadispositivo /2 + posicaomoedavetical + moedaOuro.getHeight() / 2f);


		}

		batch.end();

	}

	@Override
	//Principal método primario do projeto (Não foi necessário o uso dele para este projeto em específico)
	public void dispose() {


	}


}