package controls;

public interface CompleteSentenceControls {
    void StartGame();
    String CheckResult();
    void ToggleRadioButtons(boolean toggle);
    void ClearAllRadioButton();
    void onNextButtonClick();
    void RadioButtonClick();
}
