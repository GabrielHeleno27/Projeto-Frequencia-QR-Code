import selectors from '../elements/ElementsApp.js';

//Fluxo para ir a tela de cadastro
export async function VerificarDisciplinaAdicionada(nomeDisciplina) {
    const TelaDisciplina = await $(selectors.TelaDisciplina).getText();
    expect(TelaDisciplina).toBe('Disciplinas');

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

