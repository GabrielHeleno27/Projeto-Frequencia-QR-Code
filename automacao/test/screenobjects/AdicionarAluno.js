import selectors from '../elements/ElementsApp.js';

//Fluxo para ir a tela de adicionar
export async function TelaAdicionarAluno() {
    const TelaDisciplina = await $(selectors.TelaDisciplina).getText();
    expect(TelaDisciplina).toBe('Disciplinas');

    await expect($(selectors.MenuHamburguer)).toBeDisplayed();
    await $(selectors.MenuHamburguer).click();

    await expect($(selectors.BtnGerenciarDisciplina)).toBeDisplayed();
    await $(selectors.BtnGerenciarDisciplina).click();
}

//Fluxo Adicionar aluno com sucesso
export async function AdicionarAluno(Aluno) {
    await expect($(selectors.SelectPOO)).toBeDisplayed();

    await expect($(selectors.CmpEmailAluno)).toBeDisplayed();
    await $(selectors.CmpEmailAluno).addValue(Aluno);

    await expect($(selectors.BtnAdicionarAluno)).toBeDisplayed();
    await $(selectors.BtnAdicionarAluno).click();
}

export async function AdicionarSemPreencher() {
    await expect($(selectors.BtnAdicionarAluno)).toBeDisplayed();
    await $(selectors.BtnAdicionarAluno).click();
}

export async function VerificarToastPorTexto(mensagem) {
    await driver.pause(2000);
    const pageSource = await driver.getPageSource();
    expect(pageSource).toContain(mensagem);
}

//Fluxo verificar se a disciplina está na listagem de disciplina
export async function AlunoAdicionado(nomeAluno) {
    await expect($(selectors.MenuHamburguer)).toBeDisplayed();
    await $(selectors.MenuHamburguer).click();

    await expect($(selectors.BtnDisciplinas)).toBeDisplayed();
    await $(selectors.BtnDisciplinas).click();

    await expect($(selectors.POO)).toBeDisplayed();
    await $(selectors.POO).click();


    const elementoAluno = await $(`//android.widget.TextView[@resource-id="android:id/text1" and @text="${nomeAluno}"]`);


    const alunoVisivel = await elementoAluno.isDisplayed();

    expect(alunoVisivel).toBe(true);

    if (alunoVisivel) {
        const texto = await elementoAluno.getText();
        console.log(`${texto} foi adicionado a disciplina`);
    } else {
        console.log(`Aluno ${nomeDisciplina} não foi adicionado`);
    }

    await driver.back();
}