package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiTutorService
import com.example.model.AnatomyRepository
import com.example.model.AnatomicalSystem
import com.example.model.Organ
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuizQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val relatedOrganId: String
)

sealed interface TutorChatState {
    object Idle : TutorChatState
    object Loading : TutorChatState
    data class Success(val response: String) : TutorChatState
    data class Error(val message: String) : TutorChatState
}

enum class ActiveScreenTab {
    EXPLORE,
    QUIZ,
    AI_TUTOR
}

enum class QuizType {
    USMLE_MCQ,
    IDENTIFY_3D
}

data class AnatomyUiState(
    // App Tab Selection
    val activeTab: ActiveScreenTab = ActiveScreenTab.EXPLORE,
    
    // 3D Engine Camera Parameters
    val yaw: Float = 0.0f,     // Horizontal angle in radians
    val pitch: Float = 0.0f,   // Vertical angle in radians
    val scale: Float = 1.0f,   // Drag zoom scale
    val autoRotateSpeed: Float = 0.005f,
    val isAutoRotating: Boolean = true,
    
    // Selection Filters (By default: all except skeleton to prevent visual clutter, or all)
    val activeSystems: Set<AnatomicalSystem> = AnatomicalSystem.values().toSet(),
    val selectedOrgan: Organ? = null,
    val searchQuery: String = "",
    
    // Quiz State Machines
    val activeQuizType: QuizType = QuizType.USMLE_MCQ,
    val mcqQuestionsList: List<QuizQuestion> = emptyList(),
    val currentMcqIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isAnswerChecked: Boolean = false,
    val quizCorrectCount: Int = 0,
    val quizAttemptedCount: Int = 0,
    
    // 3D Identification Quiz State
    val identifyTargetOrgan: Organ? = null,
    val identifyFeedbackText: String = "",
    val identifyCorrect: Boolean? = null,
    
    // Gemini AI Chat State
    val geminiQuery: String = "",
    val tutorResponseState: TutorChatState = TutorChatState.Idle,
    val chatHistory: List<Pair<String, Boolean>> = emptyList() // Pair(Message, isUser)
)

class AnatomyViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AnatomyUiState())
    val uiState = _uiState.asStateFlow()

    private val highYieldQuestions = listOf(
        QuizQuestion(
            id = "q1",
            question = "A 58-year-old male presents with crushing retrosternal chest pain radiating to his jaw. A 12-lead ECG demonstrates ST-segment elevation in leads V1-V4. Which coronary artery is most likely occluded triggering this localized acute cardiac infarction?",
            options = listOf(
                "Left Anterior Descending (LAD) Artery",
                "Right Coronary Artery (RCA)",
                "Left Circumflex Artery (LCX)",
                "Posterior Descending Artery (PDA)"
            ),
            correctIndex = 0,
            explanation = "ST-segment elevation in leads V1-V4 indicates an anterior wall myocardial infarction, which is classically supplied by the Left Anterior Descending (LAD) artery. An occlusion here compromises perfusion to the anterior septum and ventricles.",
            relatedOrganId = "heart"
        ),
        QuizQuestion(
            id = "q2",
            question = "A 52-year-old chronic heavy alcoholic patient presents with hematemesis, caput medusae, and palpable splenomegaly. Bleeding from esophageal varices is identified. These dilated channels represent a collateral vascular shunt between which venous systems?",
            options = listOf(
                "Left gastric vein (portal) and Azygos vein (systemic)",
                "Superior rectal vein and Middle rectal vein",
                "Paraumbilical vein and Superficial epigastric vein",
                "Splenic vein and Renal vein"
            ),
            correctIndex = 0,
            explanation = "Esophageal varices occur when portal hypertension (cirrhosis) forces blood retrograde from the Left Gastric Vein (portal) into the Esophageal branches of the Azygos Vein (systemic). Dilated superficial submucosal veins are highly prone to rupture.",
            relatedOrganId = "liver"
        ),
        QuizQuestion(
            id = "q3",
            question = "A 19-year-old female presents with low-grade fever, anorexia, and severe localized abdominal pain at McBurney's point. Which embryological midgut structure corresponds to the anatomical landmark representing the direct attachment point of the inflamed organ?",
            options = listOf(
                "The cecal base, at the convergence of three teniae coli",
                "The descending colon, bordering the splenic flexure",
                "The terminal ileum, directly opposing the ileocecal valve",
                "The transverse colon, supplied by the middle colic artery"
            ),
            correctIndex = 0,
            explanation = "The vermiform appendix originates at the posteromedial aspect of the cecum base. Operative localization of the appendix is achieved by trailing the three longitudinal bands (taeniae coli) on the cecal wall to their point of convergence.",
            relatedOrganId = "colon"
        ),
        QuizQuestion(
            id = "q4",
            question = "A 41-year-old male presents with sudden-onset, severe, colicky flank pain radiating down to his scrotum, with hematuria. If an obstructing urinary calculus is suspected, which of the following is one of the three classic areas of physiological ureteral narrowing?",
            options = listOf(
                "At the ureteropelvic junction",
                "As the ureter crosses anterior to the renal artery",
                "The junction with the minor renal calyx neck",
                "The location where the ureter crosses behind the gonadal veins"
            ),
            correctIndex = 0,
            explanation = "The three classic anatomical sites of ureteral constriction where stones frequently impact are: (1) the ureteropelvic junction, (2) the crossing of the common iliac vessels at the pelvic brim, and (3) the ureterovesical junction (entrance into the urinary bladder).",
            relatedOrganId = "kidney_left"
        ),
        QuizQuestion(
            id = "q5",
            question = "An 80-year-old female with atrial fibrillation is admitted following a severe stroke. Post-stroke vascular neurological exam demonstrates contralateral hemiparesis and hypesthesia of her lower extremity significantly worse than her upper limb. Which cerebral artery was most likely implicated?",
            options = listOf(
                "Anterior Cerebral Artery (ACA)",
                "Middle Cerebral Artery (MCA)",
                "Posterior Cerebral Artery (PCA)",
                "Basilar Artery segment"
            ),
            correctIndex = 0,
            explanation = "The ACA supplies the anteromedial surfaces of the cerebral hemisphere (frontoparietal). The motor homunculus representing feet and legs is located medially along the longitudinal fissure, making the lower extremities more heavily affected in ACA occlusion compared to MCA.",
            relatedOrganId = "brain"
        ),
        QuizQuestion(
            id = "q6",
            question = "A 23-year-old student involved in a motor vehicle collision is found to have splenic rupture triggering massive internal bleeding. During urgent therapeutic splenectomy, which critical adjacent pancreas structure lies in direct contact with the splenic hilum and represents a high risk for accidental ligation?",
            options = listOf(
                "The tail of the pancreas within the splenorenal fold",
                "The head of the pancreas wrapping the duodenum",
                "The uncinate process adjoining the superior mesenteric artery",
                "The main pancreatic duct (Wirsung duct) junction"
            ),
            correctIndex = 0,
            explanation = "The pancreatic tail extends transversely to terminate in close proximity or direct contact with the visceral surface of the splenic hilum within the splenorenal ligament. Surgeons must carefully isolate splenic vessels without injuring the pancreatic tail.",
            relatedOrganId = "spleen"
        ),
        QuizQuestion(
            id = "q7",
            question = "A 48-year-old patient presents with acute, excruciating epigastric boring pain radiating straight through to his back, accompanied by severe vomiting. Lab analysis reveals elevated serum lipase. What is the central biochemical mechanism driving this parenchymal self-digestion?",
            options = listOf(
                "Premature enzymatic activation of intrapancreatic trypsinogen",
                "Retrograde biliary reflux of gastric hydrochloric acid",
                "Massive ischemic necrosis from pancreatic-duodenal vascular occlusion",
                "Autoimmune lymphocytic destruction of endocrine Islet beta cells"
            ),
            correctIndex = 0,
            explanation = "Acute pancreatitis is enzymatically initiated by cellular injury or duct obstruction that allows the premature activation of trypsinogen into robust trypsin inside acinar cells, triggering a cascade of tissue autodigestion, inflammatory response, and fat cell necrosis.",
            relatedOrganId = "pancreas"
        )
    )

    init {
        _uiState.update { 
            it.copy(
                mcqQuestionsList = highYieldQuestions,
                currentMcqIndex = 0
            ) 
        }
        selectRandomIdentifyTarget()
    }

    fun selectTab(tab: ActiveScreenTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    // Camera rotation and adjustments
    fun onRotate(dragAmountX: Float, dragAmountY: Float) {
        _uiState.update { state ->
            val newYaw = state.yaw + dragAmountX * 0.005f
            // Clamp pitch to avoid turning completely upside down (+- 80 degrees (~1.4 rad))
            val newPitch = (state.pitch - dragAmountY * 0.005f).coerceIn(-1.4f, 1.4f)
            state.copy(
                yaw = newYaw,
                pitch = newPitch,
                isAutoRotating = false // Pause auto-rotation when user takes conscious manual control
            )
        }
    }

    fun adjustScale(ratio: Float) {
        _uiState.update { state ->
            val newScale = (state.scale * ratio).coerceIn(0.5f, 3.0f)
            state.copy(scale = newScale)
        }
    }

    fun setAutoRotation(rotate: Boolean) {
        _uiState.update { it.copy(isAutoRotating = rotate) }
    }

    fun animateAutoRotateStep() {
        if (_uiState.value.isAutoRotating) {
            _uiState.update { state ->
                state.copy(yaw = (state.yaw + state.autoRotateSpeed) % (2f * Math.PI.toFloat()))
            }
        }
    }

    // Filtering anatomical systems
    fun toggleSystem(system: AnatomicalSystem) {
        _uiState.update { state ->
            val current = state.activeSystems
            val updated = if (current.contains(system)) {
                current - system
            } else {
                current + system
            }
            state.copy(activeSystems = updated)
        }
    }

    fun setAllSystemsEnabled(enabled: Boolean) {
        _uiState.update { state ->
            state.copy(
                activeSystems = if (enabled) AnatomicalSystem.values().toSet() else emptySet()
            )
        }
    }

    // Search and select organ
    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun selectOrgan(organ: Organ?) {
        _uiState.update { state ->
            state.copy(
                selectedOrgan = organ,
                // Center camera view somewhat when selecting an organ
                isAutoRotating = organ == null
            )
        }
        if (organ != null) {
            // Push tutor question guidance
            addChatMessage("Selected ${organ.name} (${organ.latinName}). Ask me any advanced medical questions about its structure, vasculature, or clinical pathologies below!", isUser = false)
        }
    }

    // Multiple Choice Quiz Controllers
    fun selectOption(optionIndex: Int) {
        if (_uiState.value.isAnswerChecked) return
        _uiState.update { it.copy(selectedOptionIndex = optionIndex) }
    }

    fun submitMcqAnswer() {
        _uiState.update { state ->
            val selected = state.selectedOptionIndex ?: return@update state
            val currentQ = state.mcqQuestionsList.getOrNull(state.currentMcqIndex) ?: return@update state
            val correct = selected == currentQ.correctIndex
            val newCorrectCount = if (correct) state.quizCorrectCount + 1 else state.quizCorrectCount
            state.copy(
                isAnswerChecked = true,
                quizCorrectCount = newCorrectCount,
                quizAttemptedCount = state.quizAttemptedCount + 1
            )
        }
    }

    fun advanceMcqNext() {
        _uiState.update { state ->
            val nextIdx = (state.currentMcqIndex + 1) % state.mcqQuestionsList.size
            state.copy(
                currentMcqIndex = nextIdx,
                selectedOptionIndex = null,
                isAnswerChecked = false
            )
        }
    }

    fun resetMcqScore() {
        _uiState.update { state ->
            state.copy(
                quizAttemptedCount = 0,
                quizCorrectCount = 0,
                selectedOptionIndex = null,
                isAnswerChecked = false,
                currentMcqIndex = 0
            )
        }
    }

    // 3D Spot Identification Quiz Controllers
    fun setQuizType(type: QuizType) {
        _uiState.update { it.copy(activeQuizType = type) }
        if (type == QuizType.IDENTIFY_3D) {
            selectRandomIdentifyTarget()
        }
    }

    fun selectRandomIdentifyTarget() {
        // Select a random organ from active systems to identify
        val list = AnatomyRepository.organs
        val target = list.random()
        _uiState.update { state ->
            state.copy(
                identifyTargetOrgan = target,
                identifyFeedbackText = "Rotate the 3D model, zoom in, and tap directly on the glowing orange sphere representing: '${target.name}'",
                identifyCorrect = null,
                selectedOrgan = null // clear explore selection
            )
        }
    }

    fun handle3DIdentifyTap(tappedOrgan: Organ) {
        val target = _uiState.value.identifyTargetOrgan ?: return
        if (_uiState.value.identifyCorrect != null) return // Already answered this round

        val isMatch = tappedOrgan.id == target.id
        _uiState.update { state ->
            val scoreIncr = if (isMatch) state.quizCorrectCount + 1 else state.quizCorrectCount
            state.copy(
                identifyCorrect = isMatch,
                identifyFeedbackText = if (isMatch) {
                    "CORRECT! That is indeed the ${target.name} (${target.latinName}). Well done!"
                } else {
                    "INCORRECT. You tapped the ${tappedOrgan.name}. That represents the ${tappedOrgan.system.displayName} system."
                },
                quizAttemptedCount = state.quizAttemptedCount + 1,
                quizCorrectCount = scoreIncr,
                selectedOrgan = tappedOrgan // select it so they can read details
            )
        }
    }

    // Gemini AI Tutor Chat Controllers
    fun setGeminiQuery(query: String) {
        _uiState.update { it.copy(geminiQuery = query) }
    }

    fun sendChatToTutor() {
        val query = _uiState.value.geminiQuery.trim()
        if (query.isEmpty()) return

        val organName = _uiState.value.selectedOrgan?.name ?: "General Anatomy"
        
        // Add to history
        _uiState.update { state ->
            state.copy(
                chatHistory = state.chatHistory + Pair(query, true),
                geminiQuery = "",
                tutorResponseState = TutorChatState.Loading
            )
        }

        viewModelScope.launch {
            try {
                val response = GeminiTutorService.askTutor(organName, query)
                _uiState.update { state ->
                    state.copy(
                        chatHistory = state.chatHistory + Pair(response, false),
                        tutorResponseState = TutorChatState.Success(response)
                    )
                }
            } catch (e: Exception) {
                val errMessage = e.localizedMessage ?: "Network or configuration failure."
                _uiState.update { state ->
                    state.copy(
                        tutorResponseState = TutorChatState.Error(errMessage),
                        chatHistory = state.chatHistory + Pair("Tutor Error: $errMessage. Please verify your internet connection or active tokens.", false)
                    )
                }
            }
        }
    }

    private fun addChatMessage(msg: String, isUser: Boolean) {
        _uiState.update { state ->
            state.copy(chatHistory = state.chatHistory + Pair(msg, isUser))
        }
    }

    fun clearChatHistory() {
        _uiState.update { it.copy(chatHistory = emptyList()) }
        addChatMessage("Hi! I'm your Medical AI Anatomy Companion. Tap an organ in the list, or orbit the human body and click a node, then ask any medical or licensing-board style question regarding diagnostics, pathologies, or anatomical relations!", isUser = false)
    }
}
