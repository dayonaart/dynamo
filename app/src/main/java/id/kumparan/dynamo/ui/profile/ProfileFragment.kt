package id.kumparan.dynamo.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import id.kumparan.dynamo.LoginActivity
import id.kumparan.dynamo.R
import id.kumparan.dynamo.localstorage.LocalStorage
import id.kumparan.dynamo.model.UserViewModel
import id.kumparan.dynamo.utility.ModelInjector
import id.kumparan.dynamo.utility.Utility
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {
    private val factory = ModelInjector.provideUserViewModelFactory()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val readLocalStorage = LocalStorage.readLocalToModel(requireContext())
        val userModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)
        userModel.getData().observe(viewLifecycleOwner, {
            if (it.data?.token == null) {
                tv_profile.text = readLocalStorage.toString()
            } else {
                tv_profile.text = it.toString()
            }
        })
        val loginActivity=Intent(requireContext(),LoginActivity::class.java)
        logoutBtn.setOnClickListener {
            LocalStorage.deleteSaveData(requireContext(),"user_session")
            Utility.pushReplaceAll(requireContext(),loginActivity)
        }
    }
}