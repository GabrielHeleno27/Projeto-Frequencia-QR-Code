import { GerarQrCode, GerarSemPreencher, VerificarToastPorTexto } from '../screenobjects/GerarQrCode.js';


describe('Fluxo de gerar qr code da aula', () => {

    it('Não deve gerar o QR CODE sem preencher', async () => {
        await GerarSemPreencher();
        await VerificarToastPorTexto('Selecione pelo menos um turno');
    });

    it('Deve gerar o QR CODE', async () => {
        await GerarQrCode();
    });
});