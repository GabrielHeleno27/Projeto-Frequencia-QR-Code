import selectors from '../elements/ElementsApp.js';

//Fluxo para gerar o QRCODE
export async function GerarQrCode() {
    
    await expect($(selectors.CmpData)).toBeDisplayed();
    await $(selectors.CmpData).click();

    await expect($(selectors.DataFixa)).toBeDisplayed();
    await $(selectors.DataFixa).click();

    await expect($(selectors.BtDataOk)).toBeDisplayed();
    await $(selectors.BtDataOk).click();

    await expect($(selectors.CheboxTurno)).toBeDisplayed();
    await $(selectors.CheboxTurno).click();

    const isChecked = await $(selectors.CheboxTurno).getAttribute('checked');
    expect(isChecked).toBe('true');
    console.log('O checkbox de turno está marcado corretamente.');

    await expect($(selectors.BtnGerarQRCODE)).toBeDisplayed();
    await $(selectors.BtnGerarQRCODE).click();

    await expect($(selectors.QrCodeGerado)).toBeDisplayed();
    console.log('QR GERADO!')
    await $(selectors.BtnFecharQrcode).click();
}

export async function GerarSemPreencher() {
    await expect($(selectors.DisciplinaPOO)).toBeDisplayed();
    await $(selectors.DisciplinaPOO).click();
    
    await expect($(selectors.BtnGerarQRCODE)).toBeDisplayed();
    await $(selectors.BtnGerarQRCODE).click();
}

export async function VerificarToastPorTexto(mensagem) {
    await driver.pause(2000);
    const pageSource = await driver.getPageSource();
    expect(pageSource).toContain(mensagem);
}