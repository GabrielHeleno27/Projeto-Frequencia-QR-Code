import selectors from '../elements/ElementsApp.js';

//Fluxo para ir a tela de cadastro
export async function TelaCadastroDisciplina() {
    const TelaDisciplina = await $(selectors.TelaDisciplina).getText();
    expect(TelaDisciplina).toBe('Disciplinas');

    await expect($(selectors.MenuHamburguer)).toBeDisplayed();
    await $(selectors.MenuHamburguer).click();

    await expect($(selectors.BtnGerenciarDisciplina)).toBeDisplayed();
    await $(selectors.BtnGerenciarDisciplina).click();
}

//Fluxo de cadastro com sucesso
export async function Cadastro(Disciplina, Professor) {
  await expect($(selectors.CmpNomeDisciplina)).toBeDisplayed();
  await $(selectors.CmpNomeDisciplina).addValue(Disciplina);

  await expect($(selectors.CmpEmailProf)).toBeDisplayed();
  await $(selectors.CmpEmailProf).addValue(Professor);

   await expect($(selectors.BtnCriarDisciplina)).toBeDisplayed();
  await $(selectors.BtnCriarDisciplina).click();
}

//Fluxo de cadastro sem preencher
export async function CadastroSemPreencherCampos() {
    await expect($(selectors.BtnCriarDisciplina)).toBeDisplayed();
    await $(selectors.BtnCriarDisciplina).click();
}

//Fluxo verificar se a disciplina está na listagem de disciplina
export async function TelaListagemDisciplina(nomeDisciplina) {
    await expect($(selectors.MenuHamburguer)).toBeDisplayed();
    await $(selectors.MenuHamburguer).click();

    await expect($(selectors.BtnDisciplinas)).toBeDisplayed();
    await $(selectors.BtnDisciplinas).click();

    const elementoDisciplina = await $(`//android.widget.TextView[@resource-id="com.example.frequenciaqr:id/txtNomeDisciplina" and @text="${nomeDisciplina}"]`);

    const disciplinaVisivel = await elementoDisciplina.isDisplayed();

    expect(disciplinaVisivel).toBe(true);

    if (disciplinaVisivel) {
        const texto = await elementoDisciplina.getText();
        console.log(`${texto} está na listagem`);
    } else {
        console.log(`Disciplina ${nomeDisciplina} não encontrada na listagem`);
    }
}

export async function VerificarToastPorTexto(mensagem) {
    await driver.pause(2000);
    const pageSource = await driver.getPageSource();
    expect(pageSource).toContain(mensagem);
}
