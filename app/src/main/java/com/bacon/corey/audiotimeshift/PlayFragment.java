package com.bacon.corey.audiotimeshift;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;


import libs.CircleButton;

public class PlayFragment extends Fragment {
    Bundle bundle;
    Recording recording;
    Boolean clicked = new Boolean(false);

    private OnFragmentInteractionListener mListener;


    // TODO: Rename and change types and number of parameters
    public static PlayFragment newInstance() {
        PlayFragment fragment = new PlayFragment();
        return fragment;
    }

    public PlayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
        if(bundle != null) {

            recording = (Recording) bundle.getSerializable("recording");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //TODO add color settings to buttons
        View view = inflater.inflate(R.layout.fragment_play, container, false);
        final CircleButton playButton = (CircleButton) view.findViewById(R.id.playButton);
        final CircleButton backButton = (CircleButton) view.findViewById(R.id.previousButton);
        final CircleButton forwardButton = (CircleButton) view.findViewById(R.id.nextButton);
        final CircleButton deleteButton = (CircleButton) view.findViewById(R.id.deleteButton);
        final CircleButton waveToggleButton = (CircleButton) view.findViewById(R.id.waveToggleButton);
        final int color = bundle.getInt("color");
        if(bundle != null) {
            playButton.setDefaultColor(color);
        }
        backButton.getDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY); // TODO
        forwardButton.getDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY); // TODO
        waveToggleButton.getDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY); // TODO
        deleteButton.getDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY); // TODO

        final AlphaAnimation start = new AlphaAnimation(1.0f, 0.5f);
        start.setDuration(100);

        final AlphaAnimation end = new AlphaAnimation(0.5f, 1.0f);
        end.setDuration(100);

        waveToggleButton.setTag(clicked);
        playButton.setTag(clicked);

        waveToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                start.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }
                   @Override
                    public void onAnimationEnd(Animation animation) {
                        if(!((Boolean) waveToggleButton.getTag())) {
                            waveToggleButton.setImageResource(R.drawable.ic_wave_toggle_two);
                            waveToggleButton.getDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                            waveToggleButton.setTag(new Boolean(true));
                        }
                        else if((Boolean) waveToggleButton.getTag()) {
                            waveToggleButton.setImageResource(R.drawable.ic_wave_toggle_one);
                            waveToggleButton.getDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                            waveToggleButton.setTag(new Boolean(false));
                        }
                        waveToggleButton.startAnimation(end);
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                   }
                });
              waveToggleButton.startAnimation(start);

            }
        });


        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                start.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if((Boolean)playButton.getTag() == false) {
                            playButton.setImageResource(R.drawable.ic_pause);
                            playButton.setTag(new Boolean(true));
                        }
                        else if((Boolean)playButton.getTag() == true) {
                            playButton.setImageResource(R.drawable.ic_play);
                            playButton.setTag(new Boolean(false));
                        }
                        playButton.startAnimation(end);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });


                playButton.startAnimation(start);

            }
        });



        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
