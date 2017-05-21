package com.gat.common.customview;

import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.gat.common.util.MZDebug;

/**
 * Created by mryit on 5/12/2017.
 */

/**
 * TextView.setText() before use the function: makeTextViewResizable
 * maxLine must be > 0, (maxLine = 0 may make the app be crash)
 */


public class ViewMoreTextView {

    private static final int DEFAULT_END_CHARACTERS = 0;
    private static final int DEFAULT_MAX_LINES = 5;
    private static final String DEFAULT_VIEW_MORE = "Xem thêm";
    private static final String DEFAULT_VIEW_LESS = "Ít hơn";

    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    MZDebug.w("lineEndIndex = " + lineEndIndex);

                    String text = tv.getText().toString();
                    // maxLine = 0 make app crash -> fix it when you have done the other bugs.
                    // maxLine must be >0
                    if (lineEndIndex > (expandText.length() + DEFAULT_END_CHARACTERS)) {
                        text = tv.getText().subSequence(0, lineEndIndex - expandText.length() - DEFAULT_END_CHARACTERS ) + "... " + expandText;
                    }

                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    MZDebug.w("maxLine>0, lineEndIndex = " + lineEndIndex);

                    MZDebug.w("Text comment: " + tv.getText());
                    String text = Html.fromHtml(tv.getText().toString()).toString();
                    if (lineEndIndex > (expandText.length() + DEFAULT_END_CHARACTERS)) {
                        text = tv.getText().subSequence(0, lineEndIndex - expandText.length() - DEFAULT_END_CHARACTERS) + "... " + expandText;
                    }
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else {
                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex) + "";
                    tv.setText(text);
//                    tv.setMovementMethod(LinkMovementMethod.getInstance());
//                    tv.setText(
//                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
//                                    viewMore), TextView.BufferType.SPANNABLE);
                }
            }
        });

    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                            final int maxLine, final String spannableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spannableText)) {
            ssb.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {

                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
//                        makeTextViewResizable(tv, -1, DEFAULT_VIEW_LESS, false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
//                        makeTextViewResizable(tv, maxLine, spannableText, true);
                    }

                }
            }, str.indexOf(spannableText), str.indexOf(spannableText) + spannableText.length(), 0);

        }
        return ssb;

    }

}
