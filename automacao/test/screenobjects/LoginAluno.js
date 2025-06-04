import selectors from '../elements/ElementsApp.js';


//Fluxo logout professor
export async function LogoutProfessor() {
    await expect($(selectors.MenuHamburguer)).toBeDisplayed();
    await $(selectors.MenuHamburguer).click();

    await expect($(selectors.BtnLogoutProf)).toBeDisplayed();
    await $(selectors.BtnLogoutProf).click();
}

//Login com sucesso
export async function LoginAluno(email, senha) {
    await expect($(selectors.Email)).toBeDisplayed();
    await $(selectors.Email).addValue(email);

    await expect($(selectors.Senha)).toBeDisplayed();
    await $(selectors.Senha).addValue(senha);

    await expect($(selectors.BtnEntrar)).toBeDisplayed();
    await $(selectors.BtnEntrar).click();

    const TelaDisciplina = await $(selectors.TelaDisciplina).getText();
    expect(TelaDisciplina).toBe('Disciplinas');
}