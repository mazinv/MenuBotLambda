package cz.zcu.fav.kiv.eitm.menubot;

public class LexResponse {
    private DialogAction dialogAction;

    public LexResponse(DialogAction dialogAction) {
        this.dialogAction = dialogAction;
    }

    public DialogAction getDialogAction() {
        return dialogAction;
    }
}
