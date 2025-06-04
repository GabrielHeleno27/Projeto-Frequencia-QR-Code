import { LoginActivity, LoginCamposVazios, VerificarToastPorTexto } from '../screenobjects/LoginCoordenador.js';


describe('Fluxo de Login', () => {

    it('Não deve logar deixando os campos vazios', async () => {
        await LoginCamposVazios();
        await VerificarToastPorTexto('Preencha todos os campos');
    });

    it('Não deve logar com email e senha inválidos', async () => {
        await LoginActivity('emailerrado@test.com', 'senhaerrada');
        await VerificarToastPorTexto('Email ou senha incorretos');
    });
    
    it('Deve realizar o login com sucesso', async () => {
        await LoginActivity('coordenador@email.com', '123456');
    });
});