package adapters.presenters;

import adapters.ViewModel;
import usecases.submit_word.SwOutputBoundary;
import usecases.submit_word.SwOutputDataFailure;
import usecases.submit_word.SwOutputDataValidWord;

public class SwPresenter implements SwOutputBoundary {

    private final ViewModel viewM;

    /**
     * @param viewM Instance of the view model to write to
     */
    public SwPresenter (ViewModel viewM) { this.viewM = viewM; }

    /**
     * Notify that a player with a particular ID has submitted a valid word
     * and that it has been added to the story
     * @param outputDataValidWord the wrapped output data.
     */
    @Override
    public void valid(SwOutputDataValidWord outputDataValidWord) {

    }

    /**
     * Notify that a player with a particular ID has submitted an invalid word
     * and that it has not been added to the story
     * @param outputDataFailure the wrapped output data.
     */
    @Override
    public void invalid(SwOutputDataFailure outputDataFailure) {

    }
}