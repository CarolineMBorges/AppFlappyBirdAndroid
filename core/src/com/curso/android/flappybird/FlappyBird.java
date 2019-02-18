package com.curso.android.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.lang.reflect.Field;
import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

	private SpriteBatch batch;
	//private ShapeRenderer shape;
	private Texture[] passaros;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture gameOver;

	//atributos de configuração
    private float larguraDoDispositivo;
    private float alturaDoDispositivo;
    private int estadoDoJogo = 0; //0 -> jogo não iniciado, 1 -> jogo iniciado, 2 -> jogo game over
	private int pontuacao = 0;

    private float variacao = 0;
    private float velocidadeQueda = 0;
    private float posicaoInicialVertical;
    private float posicaoMovimentoCanoHorizontal;
    private float espacoEntreCanos;
	private float deltaTime;
	private float alturaEntreCanosRandomica;

	private boolean marcouPonto;

	private Circle passaroCirculo;
	private Rectangle retanguloCanoTopo;
	private Rectangle retanguloCanoBaixo;
	private Random numeroRandomico;
	private BitmapFont font;
	private BitmapFont mensagem;

	//camera
	private OrthographicCamera camera;
	private Viewport viewport;
	private final float VIRTUAL_WIDTH = 758;
	private final float VIRTUAL_HEIGHT = 1024;



	@Override
	public void create () {
		batch = new SpriteBatch();

		numeroRandomico = new Random();

		passaroCirculo = new Circle();
		/*retanguloCanoTopo = new Rectangle();
		retanguloCanoBaixo = new  Rectangle();
		shape = new ShapeRenderer();*/

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(6);

		mensagem = new BitmapFont();
		mensagem.setColor(Color.WHITE);
		mensagem.getData().setScale(3);

		//as imagens diferentes do passaro faz com que tenhamos a impressão que ele está batendo as asas
		passaros = new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");
		gameOver = new Texture("game_over.png");

		//Configurações da Camera

		/*Inicializa uma nova instância da classe OrthographicCamera.
		 OrthographicCamera descreve uma projeção que não inclui a redução das dimensões da perspectiva
		 */
		camera = new OrthographicCamera();

		//Cada elemento de imagem e texto adicionado à tela possui uma posição de câmera definida para ele
		camera.position.set(VIRTUAL_WIDTH /2, VIRTUAL_HEIGHT /2 ,0);

		/*O viewport sempre vai ter o tamanho da janela.
		Mas a forma como os elementos são renderizados vai depender bastante do dispositivo.
		 */
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);



		fundo = new Texture("fundo.png");
		canoBaixo = new Texture("cano_baixo.png");
		canoTopo = new Texture("cano_topo.png");


        larguraDoDispositivo =  VIRTUAL_WIDTH;
        alturaDoDispositivo = VIRTUAL_HEIGHT;

        //para deixar as imagens proximas ao meio da tela
        posicaoInicialVertical = alturaDoDispositivo / 2;
        posicaoMovimentoCanoHorizontal = larguraDoDispositivo;
        espacoEntreCanos = 300;
	}

	@Override
	public void render () {

		camera.update();

		//limpar frames anteriores
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		/*
		 * getDeltaTime() é o tempo entre o início da tarefa anterior e o início da chamada atual.
		 * Ele apenas toma o tempo atual e subtrai o tempo anterior. A unidade desse valor é segundos.
		 */
		deltaTime = Gdx.graphics.getDeltaTime();
		variacao += deltaTime * 10;

		if (variacao > 2) {
			variacao = 0;
		}

		if (estadoDoJogo == 0 ){
			if(Gdx.input.justTouched()){
				estadoDoJogo = 1;
			}
		} else {

			velocidadeQueda++;
			if (posicaoInicialVertical > 0 || velocidadeQueda < 0) {
				posicaoInicialVertical = posicaoInicialVertical - velocidadeQueda;
			}

			if (estadoDoJogo == 1) {

				posicaoMovimentoCanoHorizontal -= deltaTime * 200;

				//Gdx.input.justTouched() captura o clique do mouse
				if (Gdx.input.justTouched()) {
					velocidadeQueda = -15;
				}

				//verifica se o cano saiu inteiramente da tela
				if (posicaoMovimentoCanoHorizontal < -canoTopo.getWidth()) {
					posicaoMovimentoCanoHorizontal = larguraDoDispositivo;
					alturaEntreCanosRandomica = numeroRandomico.nextInt(400) - 200;
					marcouPonto = false;
				}

				//verifica pontuação
				if(posicaoMovimentoCanoHorizontal < 120){
					if(!marcouPonto){
						pontuacao++;
						marcouPonto = true;
					}
				}
			}else{ // tela de game over

				if(Gdx.input.justTouched()){
					estadoDoJogo = 0;
					pontuacao = 0;
					velocidadeQueda = 0;
					posicaoInicialVertical = alturaDoDispositivo /2;
					posicaoMovimentoCanoHorizontal = larguraDoDispositivo;
				}

			}

		}

		//batch é a estrutura que usamos para desenhar nossas texturas
		batch.setProjectionMatrix( camera.combined );

		batch.begin();

		batch.draw(fundo,0,0, larguraDoDispositivo, alturaDoDispositivo);
		batch.draw(canoTopo , posicaoMovimentoCanoHorizontal, alturaDoDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica);
		batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal ,alturaDoDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos /2 );
		batch.draw(passaros [ (int) variacao],120,posicaoInicialVertical);

		font.draw(batch, String.valueOf(pontuacao), larguraDoDispositivo / 2, alturaDoDispositivo - 50);

		//2 -> jogo game over
		if (estadoDoJogo == 2){
			batch.draw(gameOver, larguraDoDispositivo/2 - gameOver.getWidth()/2, alturaDoDispositivo/2);
			mensagem.draw(batch, "Toque para reiniciar!", larguraDoDispositivo /2  - 200, alturaDoDispositivo /2 - gameOver.getHeight()/2 );
		}
		batch.end();

		passaroCirculo.set(120 + passaros[0].getWidth()/2, posicaoInicialVertical + passaros[0].getHeight()/2, passaros[0].getWidth()/2);
		retanguloCanoBaixo = new Rectangle(
				posicaoMovimentoCanoHorizontal, alturaDoDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos /2 ,
				canoBaixo.getWidth(), canoBaixo.getHeight()
		);

		retanguloCanoTopo = new Rectangle(
				posicaoMovimentoCanoHorizontal, alturaDoDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica,
				canoTopo.getWidth(), canoTopo.getHeight()
		);

		//Desenhar Formas
		/*shape.begin(ShapeRenderer.ShapeType.Filled);
		shape.circle(passaroCirculo.x,passaroCirculo.y,passaroCirculo.radius);
		shape.rect(retanguloCanoBaixo.x, retanguloCanoBaixo.y, retanguloCanoBaixo.width, retanguloCanoBaixo.height);
		shape.rect(retanguloCanoTopo.x,retanguloCanoTopo.y, retanguloCanoTopo.width, retanguloCanoTopo.height);
		shape.setColor(Color.RED);
		shape.end();*/

		//teste de colisão
		if ( Intersector.overlaps(passaroCirculo, retanguloCanoBaixo) || (Intersector.overlaps(passaroCirculo, retanguloCanoTopo))
				|| posicaoInicialVertical <=0 || posicaoInicialVertical >=  alturaDoDispositivo){
			estadoDoJogo = 2;
		}

	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width,height);
	}
}
