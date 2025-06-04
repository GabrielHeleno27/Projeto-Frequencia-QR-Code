import selectors from '../elements/ElementsApp.js';


//Fluxo logout coordenador
export async function LogoutCordenador() {
    await expect($(selectors.MenuHamburguer)).toBeDisplayed();
    await $(selectors.MenuHamburguer).click();

    await expect($(selectors.BntLogout)).toBeDisplayed();
    await $(selectors.BntLogout).click();
}

//Login com sucesso
export async function LoginProfessor(email, senha) {
    await expect($(selectors.Email)).toBeDisplayed();
    await $(selectors.Email).addValue(email);

    await expect($(selectors.Senha)).toBeDisplayed();
    await $(selectors.Senha).addValue(senha);

    await expect($(selectors.BtnEntrar)).toBeDisplayed();
    await $(selectors.BtnEntrar).click();

    const TelaFrequencia = await $(selectors.TelaFrequenciaQRCODE).getText();
    expect(TelaFrequencia).toBe('Frequência QR');
}