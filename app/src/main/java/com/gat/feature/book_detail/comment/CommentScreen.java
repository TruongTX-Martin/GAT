package com.gat.feature.book_detail.comment;

import com.gat.app.screen.Screen;
import com.gat.data.response.impl.EvaluationItemResponse;
import com.google.auto.value.AutoValue;

/**
 * Created by mryit on 4/23/2017.
 */
@AutoValue
public abstract class CommentScreen implements Screen{

    public static CommentScreen instance (int editionId, float value, String comment) {
        return new AutoValue_CommentScreen(editionId, value, comment);
    }

    public abstract int editionId();
    public abstract float value();
    public abstract String comment();

}
