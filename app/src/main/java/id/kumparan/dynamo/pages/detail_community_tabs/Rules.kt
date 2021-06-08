package id.kumparan.dynamo.pages.detail_community_tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import id.kumparan.dynamo.R
import id.kumparan.dynamo.model.CommunityListModel
import kotlinx.android.synthetic.main.detail_community_tab_rules.*

class Rules(private val communityListLiveData: LiveData<CommunityListModel?>,private val isModerator:Boolean):Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.detail_community_tab_rules,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (isModerator){
            changeRuleBtn.visibility=View.VISIBLE
        }
        communityListLiveData.observe(viewLifecycleOwner, {
            if (it?.rules.isNullOrEmpty()) {
                haveNoRule.visibility = View.VISIBLE
            } else {
                ruleDesc.text = it?.rules
            }
        })
    }
}