const selectors = {
    Email: '//android.widget.EditText[@resource-id="com.example.frequenciaqr:id/edtEmail"]',
    Senha: '//android.widget.EditText[@resource-id="com.example.frequenciaqr:id/edtSenha"]',
    BtnEntrar: '//android.widget.Button[@resource-id="com.example.frequenciaqr:id/btnEntrar"]',

    TelaDisciplina: '//android.widget.TextView[@text="Disciplinas"]',
    MenuHamburguer: '//android.widget.ImageButton[@content-desc="Abrir menu de navegação"]',
    BtnGerenciarDisciplina: '//androidx.appcompat.widget.LinearLayoutCompat[@resource-id="com.example.frequenciaqr:id/nav_gerenciar_disciplinas"]',
    BtnCriarDisciplina: '//android.widget.Button[@resource-id="com.example.frequenciaqr:id/btnCriarDisciplina"]',
    CmpNomeDisciplina: '//android.widget.EditText[@resource-id="com.example.frequenciaqr:id/edtNomeDisciplina"]',
    CmpEmailProf: '//android.widget.EditText[@resource-id="com.example.frequenciaqr:id/edtEmailProfessor"]',
    SelectSemestre: '//android.widget.Spinner[@resource-id="com.example.frequenciaqr:id/spinnerSemestre"]',
    BtnDisciplinas: '//android.widget.EditText[@resource-id="com.example.frequenciaqr:id/edtNomeDisciplina"]',
    POO: '//android.widget.TextView[@resource-id="com.example.frequenciaqr:id/txtNomeDisciplina" and @text="POO"]',
    SelectPOO:'//android.widget.TextView[@resource-id="android:id/text1" and @text="POO (2025.1)"]',
    CmpEmailAluno: '//android.widget.EditText[@resource-id="com.example.frequenciaqr:id/edtEmailAluno"]',
    BtnAdicionarAluno: '//android.widget.Button[@resource-id="com.example.frequenciaqr:id/btnAdicionarAluno"]',
    AlunoADD: '//android.widget.TextView[@resource-id="android:id/text1"]',

    BntLogout: '//androidx.appcompat.widget.LinearLayoutCompat[@resource-id="com.example.frequenciaqr:id/nav_logout"]',
    TelaFrequenciaQRCODE: '//android.widget.TextView[@text="Frequência QR"]',
    DisciplinaPOO: '//androidx.recyclerview.widget.RecyclerView[@resource-id="com.example.frequenciaqr:id/recyclerViewDisciplinas"]/androidx.cardview.widget.CardView[1]/android.widget.LinearLayout',
    CmpData: '//android.widget.EditText[@resource-id="com.example.frequenciaqr:id/edtData"]',
    DataFixa: '//android.view.View[@content-desc="06 junho 2025"]',
    BtDataOk: '//android.widget.Button[@resource-id="android:id/button1"]',
    CheboxTurno: '//android.widget.CheckBox[@resource-id="com.example.frequenciaqr:id/turnoA"]',
    BtnGerarQRCODE: '//android.widget.Button[@resource-id="com.example.frequenciaqr:id/btnGerarQR"]',
    QrCodeGerado: '//android.widget.ImageView[@content-desc="QR Code gerado"]',
    BtnFecharQrcode: '//android.widget.Button[@resource-id="com.example.frequenciaqr:id/btnFechar"]',
    BtnLogoutProf: '//androidx.appcompat.widget.LinearLayoutCompat[@resource-id="com.example.frequenciaqr:id/nav_logout"]'
    



    

};

export default selectors;