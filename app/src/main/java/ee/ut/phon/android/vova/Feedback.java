package ee.ut.phon.android.vova;

/**
 *
 * File: Feedback.java
 *
 * Description: Object used to give feedback to user.
 * Each object contains short description and long description of the feedback.
 *
 * Author: Jürgen Leppsalu
 * Date: 04.05.2021
 *
 */

public class Feedback {

    boolean canAudibleFeedbackBeGiven;
    String feedback;
    String feedbackShort;

    /// FEEDBACKS WHEN APPLICATION ENCOUNTERS AN ERROR
    public static Feedback getMicrophoneErrorFeedback() {
        return new Feedback("Rakendus ei saa kasutada mikrofoni. Veenduge, et mikrofon töötab ja rakendusel on õigus seda kasutada.", "Viga!\nRakendus ei saa ligipääsu mikrofonile.");
    }

    public static Feedback getDeviceNetworkErrorFeedback() {
        Feedback fb = new Feedback("Seadmel on probleeme interneti teel serveritega suhtlemisel. Võtke ühendust klienditoega.", "Viga!\nSeade ei saa suhtlda üle interneti serveritega.");
        fb.setCanAudibleFeedbackBeGiven(false);
        return fb;
    }

    public static Feedback getNoInternetConnectionErrorFeedback() {
        Feedback fb = new Feedback("Seadmel puudub ühendus internetiga. Palun looge ühendus internetiga!", "Viga!\nÜhendus internetiga puudub.");
        fb.setCanAudibleFeedbackBeGiven(false);
        return fb;
    }

    public static Feedback getTranscriptonServerErrorFeedback() {
        return new Feedback("Seade ei saa ühendust kõne transkribeerimise serveriga. Palun proovige hiljem uuesti. Kui viga kordub, siis võtke ühendust klienditoega.", "Viga!\nKõne ei saa transkribeerida.");
    }

    public static Feedback getSpeechAnalysisServerResponseErrorFeedback() {
        return new Feedback("Seade ei saa ühendust kõne töötlemise serveriga. Palun proovige hiljem uuesti. Kui viga kordub, siis võtke ühendust klienditoega.", "Viga!\nKõne ei saa töödelda.");
    }

    public static Feedback getUnknownErrorFeedback() {
        return new Feedback("Rakenduse töös esines tundmatu tõrge. Palun võtke ühendust klienditoega.", "Viga!\nTundmatu tõrge.");
    }

    public static Feedback getUnknownErrorFeedback(boolean canAudibleFeedbackBeGiven) {
        Feedback fb = new Feedback("Rakenduse töös esines tundmatu tõrge. Palun võtke ühendust klienditoega.", "Viga!\nTundmatu tõrge.");
        fb.setCanAudibleFeedbackBeGiven(canAudibleFeedbackBeGiven);
        return fb;
    }

    /// FEEDBACKS WHEN APPLICATION RECEIVES UNKNOWN VALUES
    public static Feedback getNoTranscriptionResultsFeedback() {
        return new Feedback("Salvestust ei suudetud töödelda. Palun liikuge mürast vabamasse keskkonda või esitage käsklus selgemalt.", "Kõne oli ebaselge või salvestus liiga mürane.");
    }

    public static Feedback getUnknownCommandFeedback() {
        return new Feedback("Käsklust ei tuvastatud. Palun esitage rakendusele käsklus, mida rakendus toetab, või sõnastage oma senine käsklus ümber.", "Käsklusega seotud tegevust ei tuvastatud.");
    }

    // / FEEDBACKS WHEN APPLICATION CAN SUCCESSFULLY PROCESS USER REQUEST
    public static Feedback getStartMediaCommandSuccessfulFeedback() {
        return new Feedback("Käivitan meediapleieri.", "Käivitati meediapleier.");
    }

    public static Feedback getStopMediaCommandSuccessfulFeedback() {
        return new Feedback("Peatan meediapleieri.", "Peatati meediapleier.");
    }

    public static Feedback getSearchCommandSuccessful(String query) {
        return new Feedback("Teen interneti järgneva päringu: "+query, "Käivitati otsing.");
    }

    public static Feedback getReminderCommandSuccessful(String datetime) {
        // TODO: Kasutada ära datetime stringi tagasiside andmisel.
        return new Feedback("Seadistasin meeldetuletuse.", "Sean meeldetuletuse");
    }

    public static Feedback getIncreaseVolumneCommandSuccessful(int percentage) {
        return new Feedback("Tõstan helitugevust " + String.valueOf(percentage) + "protsendi võrra.", "Tõsteti helitugevust.");
    }

    public static Feedback getDecreaseVolumeCommandSuccessful(int percentage) {
        return new Feedback("Vähendan helitugevust " + percentage + "protsendi võrra.", "Vähendati helitugevust.");
    }

    // Non-static class methods.

    public Feedback(String feedback, String feedbackShort) {
        this.feedback = feedback;
        this.feedbackShort = feedbackShort;
        this.canAudibleFeedbackBeGiven=true;
    }

    public void setCanAudibleFeedbackBeGiven(boolean canAudibleFeedbackBeGiven) {
        this.canAudibleFeedbackBeGiven = canAudibleFeedbackBeGiven;
    }

    public void setFeedback(String feedback, String feedbackShort) {
        this.feedback = feedback;
        this.feedbackShort = feedbackShort;
    }

    public String getFeedback() {
        return feedback;
    }

    public String getShortenedFeedback() {
        return feedbackShort;
    }

}
