import selectors from '../elements/ElementsApp.js';

//Login com sucesso
export async function LoginActivity(email, senha) {
  await expect($(selectors.Email)).toBeDisplayed();
  await $(selectors.Email).addValue(email);

  await expect($(selectors.Senha)).toBeDisplayed();
  await $(selectors.Senha).addValue(senha);

   await expect($(selectors.BtnEntrar)).toBeDisplayed();
  await $(selectors.BtnEntrar).click();
}

//Login sem preencher os campos
export async function LoginCamposVazios() {
    await expect($(selectors.BtnEntrar)).toBeDisplayed();
    await $(selectors.BtnEntrar).click();
}

//Verificar as mensagens
export async function VerificarToastPorTexto(mensagem) {
    await driver.pause(2000);
    const pageSource = await driver.getPageSource();
    expect(pageSource).toContain(mensagem);
}