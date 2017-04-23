package com.gat.feature.book_detail.comment;

import com.gat.app.screen.Screen;
import com.gat.data.response.impl.EvaluationItemResponse;
import com.google.auto.value.AutoValue;

/**
 * Created by mryit on 4/23/2017.
 */
@AutoValue
public abstract class CommentScreen implements Screen{

    public static CommentScreen instance (EvaluationItemResponse evaluation) {
        return new AutoValue_CommentScreen(evaluation);
    }

    public abstract EvaluationItemResponse evaluation();

}
