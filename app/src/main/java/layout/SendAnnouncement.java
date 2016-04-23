package layout;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.csci342.justin.moodleapplication.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SendAnnouncement.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SendAnnouncement#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SendAnnouncement extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    View rootView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SendAnnouncement() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SendAnnouncement.
     */
    // TODO: Rename and change types and number of parameters
    public static SendAnnouncement newInstance(String param1, String param2) {
        SendAnnouncement fragment = new SendAnnouncement();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_send_announcement, container, false);

        /*
        final ListView listview2 = (ListView) rootView.findViewById(R.id.SA_list_listview);

        String[] values = new String[] {"Dummy Data 1", "Dummy Data 2", "Dummy Data 3", "Dummy Data 4", "Dummy Data 5"};

        final ArrayList<String> list2 = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
            list2.add(values[i]);
        }

        final ArrayAdapter adapter2 = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, list2);
        listview2.setAdapter(adapter2);
        */

        String[] values = new String[] {"Dummy Data 1", "Dummy Data 2", "Dummy Data 3", "Dummy Data 4", "Dummy Data 5"};

        LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.SA_linear_layout);

        for (int i = 0; i < values.length; ++i)
        {
            TextView txt1 = new TextView(getActivity());
            txt1.setText(values[i]);
            linearLayout.addView(txt1);
        }

        FrameLayout f2 = (FrameLayout) getActivity().findViewById(R.id.D_tabview_framelayout);

        RelativeLayout.LayoutParams x = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300);
        x.addRule(RelativeLayout.BELOW, R.id.D_tabslist_linearlayoutvertical);
        x.addRule(RelativeLayout.ABOVE, R.id.D_VieUni_button);


        f2.setLayoutParams(x);


        return rootView;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void NewAnnoucementButton()
    {

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
        void onFragmentInteraction(Uri uri);
    }
}
