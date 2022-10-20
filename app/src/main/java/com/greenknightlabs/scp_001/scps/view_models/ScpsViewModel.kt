package com.greenknightlabs.scp_001.scps.view_models

import com.greenknightlabs.scp_001.app.view_models.PageViewModel
import com.greenknightlabs.scp_001.scps.models.Scp
import com.greenknightlabs.scp_001.scps.view_holders.ScpComponentViewHolder

abstract class ScpsViewModel : PageViewModel<Scp>(), ScpComponentViewHolder.Listener { }
