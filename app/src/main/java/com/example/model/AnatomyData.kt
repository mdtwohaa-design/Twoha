package com.example.model

import com.example.engine.Vector3D

enum class AnatomicalSystem(val displayName: String, val colorHex: String) {
    SKELETAL("Skeletal", "#B39DDB"),      // Deep violet
    NERVOUS("Nervous", "#BBDEFB"),       // Brain/nerve cobalt blue
    CARDIOVASCULAR("Cardio", "#EF9A9A"),  // High-contrast red
    RESPIRATORY("Respiratory", "#A5D6A7"), // Oxygen teal/green
    DIGESTIVE("Digestive", "#FFCC80"),    // Gastrointestinal gold
    URINARY("Urinary", "#FFF59D"),       // Acid yellow
    ENDOCRINE("Endocrine", "#F48FB1")     // Hormonal pink
}

data class Pathology(
    val name: String,
    val presentation: String,
    val pathologyDescription: String
)

data class Organ(
    val id: String,
    val name: String,
    val latinName: String,
    val system: AnatomicalSystem,
    val position3D: Vector3D,
    val radius: Float = 0.08f, // physical interactive size
    val description: String,
    val detailedStructure: List<String>,
    val functions: List<String>,
    val clinicalSignificance: List<String>,
    val pathologies: List<Pathology>
)

object AnatomyRepository {
    val organs = listOf(
        Organ(
            id = "brain",
            name = "Brain",
            latinName = "Encephalon",
            system = AnatomicalSystem.NERVOUS,
            position3D = Vector3D(0.0f, 1.80f, 0.0f),
            description = "The central organ of the human nervous system, responsible for coordinating sensory feedback, motor command execution, executive decision-making, and memory storage.",
            detailedStructure = listOf(
                "Cerebral cortex (split into frontal, parietal, temporal, and occipital lobes)",
                "Cerebellum (coordinates motor movement, balance, and fine adjustments)",
                "Brainstem (Midbrain, Pons, and Medulla Oblongata - vital cardiorespiratory centers)",
                "Limbic System (including Hippocampus for memory consolidation and Amygdala for emotional processing)",
                "Ventricular System (produces and circulates Cerebrospinal Fluid [CSF])"
            ),
            functions = listOf(
                "Sensory translation and motor planning integration",
                "Cognition, language processing (Broca's and Wernicke's areas), and executive behavior",
                "Autonomous cardiovascular and respiratory reflex regulation",
                "Homeostatic endocrine control via the Hypothalamus-Pituitary Axis"
            ),
            clinicalSignificance = listOf(
                "Cerebrovascular Accidents (CVAs): Ischemic strokes (anterior/middle/posterior cerebral artery thrombosis) or Hemorrhagic strokes commonly presenting as sudden onset focal neurological deficits.",
                "Increased Intracranial Pressure (ICP): Cushing's Triad (Bradycardia, Hypertension, irregular breathing) signaling imminent brain herniation.",
                "Cranial Nerve Palsies: Pathologies localizing brainstem strokes, cavernous sinus syndromes, or intracranial space-occupying lesions."
            ),
            pathologies = listOf(
                Pathology(
                    name = "Ischemic Stroke",
                    presentation = "Contralateral hemiparesis, sensory loss, facial droop, and expressive/receptive aphasia (if dominant hemisphere is affected).",
                    pathologyDescription = "Arterial occlusion resulting in cerebral tissue ischemia, subsequent cytotoxic edema, and liquefactive necrosis."
                ),
                Pathology(
                    name = "Bacterial Meningitis",
                    presentation = "Fever, altered mental status, photophobia, nuchal rigidity, positive Kernig's and Brudzinski's signs.",
                    pathologyDescription = "Acute inflammation of the leptomeninges (pia and arachnoid mater) driven by pathogens like S. pneumoniae or N. meningitidis, yielding elevated CSF opening pressures, high protein, and low glucose."
                )
            )
        ),
        Organ(
            id = "thyroid",
            name = "Thyroid Gland",
            latinName = "Glandula Thyroidea",
            system = AnatomicalSystem.ENDOCRINE,
            position3D = Vector3D(0.0f, 1.45f, 0.14f),
            description = "A highly vascularized ductless endocrine gland situated anteriorly in the neck, wrapped around the trachea.",
            detailedStructure = listOf(
                "Left and Right Lobes connected across the second and third tracheal rings by an Isthmus",
                "Pyramidal Lobe (embryological remnant of the thyroglossal duct, present in ~50% of people)",
                "Follicular Cells (producing T3 and T4 hormones under TSH stimulus)",
                "Parafollicular (C) cells (synthesizing Calcitonin to assist calcium homeostatic lowering)"
            ),
            functions = listOf(
                "Synthesizes and secretes active T3 (Triiodothyronine) and T4 (Thyroxine) to regulate basal metabolic rate",
                "Enhances body sensitivity to catecholamines by upregulating beta-adrenergic receptors",
                "Participates in pediatric growth and skeletal/neural development"
            ),
            clinicalSignificance = listOf(
                "Goiter: Enlargement of the gland induced by iodine deficiency or autoimmune stimulation.",
                "Recurrent Laryngeal Nerve damage: A high-risk surgical hazard during thyroidectomies, leading to postoperative vocal cord paralysis and hoarseness.",
                "Thyroid Storm: Life-threatening decompensated hyperthyroidism requiring beta-blockade, propylthiouracil, and steroids."
            ),
            pathologies = listOf(
                Pathology(
                    name = "Hashimoto's Thyroiditis",
                    presentation = "Cold intolerance, weight gain, fatigue, dry skin, bradycardia, diffuse painless goiter.",
                    pathologyDescription = "Autoimmune destruction of thyroid follicular cells mediated by anti-thyroid peroxidase (anti-TPO) and anti-thyroglobulin antibodies, histologically characterized by germinal centers and Hurthle cells."
                ),
                Pathology(
                    name = "Graves' Disease",
                    presentation = "Heat intolerance, weight loss, palpitation, pretibial myxedema, and exophthalmos (proptosis).",
                    pathologyDescription = "Autoantibody (Thyroid Stimulating Immunoglobulin) binds to and activates TSH receptors on thyroid follicular cells, prompting autonomous, unregulated hyperthyroidism."
                )
            )
        ),
        Organ(
            id = "heart",
            name = "Heart",
            latinName = "Cor",
            system = AnatomicalSystem.CARDIOVASCULAR,
            position3D = Vector3D(-0.1f, 1.05f, 0.17f),
            description = "A muscular, four-chambered double pump positioned obliquely in the middle mediastinum to propel oxygenated and deoxygenated blood throughout the body.",
            detailedStructure = listOf(
                "Left and Right Atria (recipients of venous return) and Ventricles (high-pressure pump chambers)",
                "Atrioventricular (Tricuspid/Mitral) and Semilunar (Pulmonic/Aortic) valves preventing backflow",
                "Cardiac Conduction System: Sinoatrial (SA) node, Atrioventricular (AV) node, Bundle of His, and Purkinje fibers",
                "Coronary Circulation: Left Main (bifurcating into LAD and Circumflex) and Right Coronary Arteries"
            ),
            functions = listOf(
                "Pumps blood through the pulmonary circulatory system for gas exchange",
                "Drives systemic arterial pressure to perfuse vital visceral beds",
                "Secretes Atrial Natriuretic Peptide (ANP) in response to atrial volume overload"
            ),
            clinicalSignificance = listOf(
                "Myocardial Infarction (MI): Occlusion of coronary arteries triggering coagulative necrosis. Classic signs include ST-elevation on ECG and elevated serum troponins.",
                "Cardiac Tamponade: Accumulation of pericardial fluid compressing chambers; presenting with Beck's Triad (Hypotension, jugular venous distention, muffled heart sounds).",
                "Valvular Heart Disease: Mitral stenosis (diastolic rumble) or Aortic stenosis (systolic crescendo-decrescendo murmur radiating to carotids)."
            ),
            pathologies = listOf(
                Pathology(
                    name = "Coronary Artery Disease (CAD)",
                    presentation = "Exertional retrosternal crushing chest pain radiating to left arm/jaw, relieved by rest or nitroglycerine (Angina Pectoris).",
                    pathologyDescription = "Fibrous atherosclerotic plaque accumulation in coronary arteries narrowing the vessel lumen and creating supply-demand ischemia mismatch."
                ),
                Pathology(
                    name = "Congestive Heart Failure (CHF)",
                    presentation = "Dyspnea on exertion, orthopnea, paroxysmal nocturnal dyspnea, bilateral pedal edema, pulmonary rales, S3 gallop.",
                    pathologyDescription = "Inability of cardiac output to meet systemic metabolic demands, yielding retrograde congestion of pulmonary capillaries (left-sided) or systemic veins (right-sided)."
                )
            )
        ),
        Organ(
            id = "lung_left",
            name = "Left Lung",
            latinName = "Pulmo Sinister",
            system = AnatomicalSystem.RESPIRATORY,
            position3D = Vector3D(-0.24f, 1.03f, 0.12f),
            description = "The left primary organ of respiration, structurally adapted to share space with the cardiac apex within the left hemithorax.",
            detailedStructure = listOf(
                "Divided into two lobes: Superior and Inferior, separated by an oblique fissure",
                "Contains the Cardiac Notch and Lingula (an anatomical analog of the right middle lobe)",
                "Hilum containing the left main bronchus, left pulmonary artery, and two left pulmonary veins",
                "Dual bronchopulmonary segments supplied by segmental bronchi"
            ),
            functions = listOf(
                "Enables passive and active alveolar gas exchange (Oxygen uptake, Carbon dioxide excretion)",
                "Participates in acid-base balance by adjusting metabolic expiration of volatile carbon dioxide",
                "Filters circulating microtrombi via pulmonary capillary endothelial beds"
            ),
            clinicalSignificance = listOf(
                "Pneumothorax: Air accumulation in the pleural space collapsing the lung. Presents with hyperresonance and absent breath sounds locally.",
                "Pulmonary Embolism (PE): Occlusion of the pulmonary vasculature, typically originating from a deep vein thrombosis; presents with pleuritic chest pain, acute dyspnea, and tachycardia.",
                "Aspiration Pneumonia: Predilection for localized segments. When supine, posterior segments of the upper lobes are highly vulnerable."
            ),
            pathologies = listOf(
                Pathology(
                    name = "Pneumonitis & Pneumonia",
                    presentation = "Fever, productive cough with purulent sputum, pleuritic chest pain, localized tactile fremitus, and dullness on percussion.",
                    pathologyDescription = "Infectious exudative consolidation of the pulmonary parenchyma fills alveolar spaces with polymorphonuclear leukocytes, resolving in ventilation-perfusion mismatch."
                ),
                Pathology(
                    name = "COPD (Emphysema)",
                    presentation = "Progressive dyspnea, pursed-lip breathing, barrel chest, minimal cough (classical pink puffer presentation).",
                    pathologyDescription = "Permanent destruction and enlargement of airspace walls distal to terminal bronchioles without fibrosis, driven by cigarette smoke or alpha-1 antitrypsin deficiency, causing loss of lung elastic recoil."
                )
            )
        ),
        Organ(
            id = "lung_right",
            name = "Right Lung",
            latinName = "Pulmo Dexter",
            system = AnatomicalSystem.RESPIRATORY,
            position3D = Vector3D(0.24f, 1.03f, 0.12f),
            description = "The larger, highly functional right organ of respiration, occupying the right hemithorax.",
            detailedStructure = listOf(
                "Divided into three lobes: Superior, Middle, and Inferior lobes",
                "Separated by the Horizontal fissure (between superior/middle) and Oblique fissure (between middle/inferior)",
                "A shorter, wider, and more vertical Right Main Bronchus (major foreign body aspiration liability)",
                "Hilum showing anterior/inferior pulmonary veins and superior bronchus structure"
            ),
            functions = listOf(
                "Primary driver for exchange of air, oxygenating mixed venous blood",
                "Performs chemical active synthesis (such as endothelial cell pulmonary conversion of Angiotensin I to Angiotensin II via ACE)",
                "Regulates immunological mucosal airway defense with secretory IgA and alveolar macrophages"
            ),
            clinicalSignificance = listOf(
                "Foreign Body Aspiration: Tends to enter the right bronchus due to its more vertical orientation and wider lumen compared to the left.",
                "Pleural Effusion: Fluid buildup in the costodiaphragmatic recess. Appears clinically as blunted costophrenic angles on chest X-ray.",
                "Lobular Consolidation: Anatomical boundaries easily traced on diagnostic physical exam."
            ),
            pathologies = listOf(
                Pathology(
                    name = "Lobar Pneumonia",
                    presentation = "High fever, chills, cough with blood-tinged sputum, bronchial breath sounds, and crackles.",
                    pathologyDescription = "Acute bacterial consolidation of an entire single lobe (often S. pneumoniae), progressing through classic stages: Congestion, Red Hepatization, Gray Hepatization, and Resolution."
                ),
                Pathology(
                    name = "Tuberculosis (Primary & Secondary)",
                    presentation = "Chronic cough, evening hemoptysis, night sweats, low-grade fever, weight loss, cavitation in the upper lung lobes.",
                    pathologyDescription = "Infection by Mycobacterium tuberculosis triggering a cellular type IV hyperresponsiveness, consolidating as subpleural Ghon foci and hilar lymph node calcification (Ghon complex)."
                )
            )
        ),
        Organ(
            id = "liver",
            name = "Liver",
            latinName = "Hepar",
            system = AnatomicalSystem.DIGESTIVE,
            position3D = Vector3D(0.18f, 0.72f, 0.14f),
            description = "The largest internal organ and visceral gland in the human body, situated in the right hypochondrium under the diaphragm, handling continuous metabolic filtration and bile production.",
            detailedStructure = listOf(
                "Anatomical lobes: Right, Left, Caudate, and Quadrate lobes",
                "Boundaries outlined by Falciform, Ligamentum Teres, and Coronary ligaments",
                "Functional Hepatic Lobules containing radially arranged cords of hepatocytes around a Central Vein",
                "The Portal Triad (Hepatic Portal Vein, Proper Hepatic Artery, Common Bile Duct) at the porta hepatis"
            ),
            functions = listOf(
                "Bile salt synthesis for lipid emulsification and fat-soluble vitamin absorption",
                "Carbohydrate metabolism (Glycogenesis, Glycogenolysis, Gluconeogenesis)",
                "Detoxification of endogenous nitrogen (Urea Cycle) and exogenous xenobiotics",
                "Synthesis of critical plasma proteins, including Albumin, transferrin, and Clotting Factors (II, VII, IX, X)"
            ),
            clinicalSignificance = listOf(
                "Portal Hypertension: High venous opposition within the liver, causing retrograde collateral shunts presenting as esophageal varices, caput medusae, and internal hemorrhoids.",
                "Hepatic Encephalopathy: Failure of hepatocytes to convert neurotoxic ammonia into urea, resulting in cerebral edema, asterixis (flapping tremor), and coma.",
                "Coagulopathy: Severe liver damage reduces production of clotting factors, measured via prolonged Prothrombin Time (PT/INR)."
            ),
            pathologies = listOf(
                Pathology(
                    name = "Liver Cirrhosis",
                    presentation = "Jaundice, ascites, splenomegaly, palmar erythema, spider angiomas, gynecomastia, asterixis.",
                    pathologyDescription = "Diffuse hepatic architectural distortion. Star-shaped Ito (stellate) cells secrete collagen when chronically injured, replacing normal parenchyma with regenerative nodules and fibrous scar tissue."
                ),
                Pathology(
                    name = "Hepatocellular Carcinoma",
                    presentation = "Acanthosis, dull right upper quadrant abdominal pain, weight loss, cachexia, rising serum alpha-fetoprotein (AFP).",
                    pathologyDescription = "Malignant neoplasm of hepatocellular lineage, highly associated with chronic Hepatitis B/C, alcoholic cirrhosis, and non-alcoholic steatohepatitis (NASH)."
                )
            )
        ),
        Organ(
            id = "stomach",
            name = "Stomach",
            latinName = "Gaster",
            system = AnatomicalSystem.DIGESTIVE,
            position3D = Vector3D(-0.16f, 0.68f, 0.14f),
            description = "A dilated, highly distensible J-shaped intraperitoneal muscular pouch functioning as the primary reservoir for mechanical churning and acidic enzymatic digestion.",
            detailedStructure = listOf(
                "Four primary regions: Cardia, Fundus, Body, and Pylorus",
                "Bounded by the Lesser Curvature and Greater Curvature",
                "Gastric mucosa containing deep folds (Rugae) that flatten upon distension",
                "Gastric glands hosting Parietal cells (H+/K+ ATPase, Intrinsic Factor), Chief cells (pepsinogen), and G-cells (gastrin)"
            ),
            functions = listOf(
                "Mechanical reduction of food into liquid acid chyme",
                "Protein digestion via activation of pepsinogen to pepsin under low pH conditions",
                "Secretions of Intrinsic Factor, essential for distal ileal absorption of Vitamin B12"
            ),
            clinicalSignificance = listOf(
                "Peptic Ulcer Disease (PUD): Mucosal erosion penetrating the muscularis mucosa, frequently caused by H. pylori or NSAIDs.",
                "Pyloric Stenosis: Infantile hypertrophy of the smooth muscle, characterized by projectile, non-bilious emesis.",
                "Gastric Adenocarcinoma (Virchow's Node): Lymphatic metastasis draining retrograde into the left supraclavicular lymph node."
            ),
            pathologies = listOf(
                Pathology(
                    name = "Peptic Ulcer Disease",
                    presentation = "Epigastric pain. Duodenal ulcers are classically relieved by food, while gastric ulcers are aggravated by food (leading to weight loss).",
                    pathologyDescription = "Loss of protective mucosal integrity (driven by chronic COX-1 pathway NSAID inhibition or H. pylori mucosal cytotoxins) allowing hydrochloric acid autodigestion of subepithelial layers."
                ),
                Pathology(
                    name = "Gastroesophageal Reflux Disease (GERD)",
                    presentation = "Heartburn, regurgitation, dysphagia, chronic dry nocturnal cough, microgastrointestinal erosion.",
                    pathologyDescription = "Lower esophageal sphincter (LES) transient relaxation or incompetence, backing gastric acid into the esophagus and predisposing to columnar metaplasia (Barrett's Esophagus)."
                )
            )
        ),
        Organ(
            id = "spleen",
            name = "Spleen",
            latinName = "Lien",
            system = AnatomicalSystem.CARDIOVASCULAR, // also lymphatic, can categorize appropriately
            position3D = Vector3D(-0.35f, 0.70f, 0.08f),
            description = "The largest encapsulated lymphoid organ in the human body, positioned subcutaneously in the left hypochondriac region abutting ribs 9-11.",
            detailedStructure = listOf(
                "Splenic Capsule composed of dense irregular connective tissue",
                "White Pulp: Dense nests of B-lymphocytes (follicles) and T-lymphocytes (Periarteriolar Lymphatic Sheaths [PALS])",
                "Red Pulp: Splenic cords (Cords of Billroth) and venous sinusoids lined by fenestrated endothelial cells",
                "Splenic Artery (branch of the celiac trunk) running a highly tortuous sub-pancreatic course"
            ),
            functions = listOf(
                "Filters circulating bloodstream pathogens and damaged, senescent Erythrocytes",
                "Mounts active humoral immune responses by presenting blood-borne antigens",
                "Maintains a circulating reservoir of platelets and monocytes",
                "Synthesizes opsonins (Tuftsin, properdin, and specific IgM antibodies)"
            ),
            clinicalSignificance = listOf(
                "Splenic Rupture: Complicated by blunt abdominal trauma (e.g., motor vehicle collisions) or infectious mononucleosis, leading to massive hemoperitoneum and left shoulder tip pain (Kehr's Sign).",
                "Asplenia / Post-splenectomy: Disastrously elevates susceptibility to encapsulated organisms (S. pneumoniae, N. meningitidis, H. influenzae) due to loss of splenic opsonizing filtration. Requires vaccination.",
                "Splenomegaly: Secondary to portal hypertension or chronic hemolytic anemias, causing splenic sequestration of platelets."
            ),
            pathologies = listOf(
                Pathology(
                    name = "Splenic Infarction",
                    presentation = "Sudden-onset left upper quadrant abdominal pain radiating to the left shoulder, fever, and peritoneal signs.",
                    pathologyDescription = "Occlusion of the splenic artery or its segmental branches, often caused by embolic events (such as infective endocarditis or atrial fibrillation) or vaso-occlusive sickling crises."
                ),
                Pathology(
                    name = "Infectious Mononucleosis Splenomegaly",
                    presentation = "Sore throat, high fever, atypical lymphocytosis, profound fatigue, massive cervical lymphadenopathy.",
                    pathologyDescription = "Epstein-Barr Virus (EBV) infects B-lymphocytes, driving massive hypertrophic expansion of CD8+ cytotoxic T-cells in the splenic periarteriolar lymphatic sheaths (PALS) and follicles, thinning the capsule."
                )
            )
        ),
        Organ(
            id = "kidney_left",
            name = "Left Kidney",
            latinName = "Ren Sinister",
            system = AnatomicalSystem.URINARY,
            position3D = Vector3D(-0.20f, 0.45f, -0.14f),
            description = "A bean-shaped, retroperitoneal organ located near the T12-L3 vertebral levels, serving as the primary filter for systemic blood volume.",
            detailedStructure = listOf(
                "Renal Cortex containing renal corpuscles, proximal and distal convoluted tubules",
                "Renal Medulla containing renal pyramids, Loop of Henle, and collecting ducts",
                "Minor and Major Calyces draining urine into the Renal Pelvis and Ureter",
                "Hilum situated medially, receiving the renal artery, renal vein, and ureter"
            ),
            functions = listOf(
                "Filters metabolic waste products (urea, creatinine, uric acid) from the blood",
                "Reabsorbs essential electrolytes and water to regulate intravascular extracellular volume",
                "Produces Erythropoietin (EPO) in response to chronic hypoxia",
                "Secretes Renin from juxtaglomerular cells to modulate systemic arterial blood pressure"
            ),
            clinicalSignificance = listOf(
                "Nephrolithiasis: Kidney stones (calcium oxalate, struvite, uric acid) obstructing the ureter, inducing severe colicky flank pain radiating to the groin.",
                "Renal Artery Stenosis: Narrowing of the renal artery triggering chronic hypoperfusion, excess renin secretion, and secondary hypertension.",
                "Glomerulonephritis: Deposition of immune complexes (e.g. post-streptococcal) leading to hematuria, proteinuria, oliguria, and facial edema."
            ),
            pathologies = listOf(
                Pathology(
                    name = "Nephrolithiasis",
                    presentation = "Sudden onset severe, sharp, crescendo-decrescendo flank pain radiating to the groin, accompanied by hematuria, nausea, and vomiting.",
                    pathologyDescription = "Urine supersaturation with stone-forming salts leading to crystal precipitation and eventual physical mechanical impaction and obstruction in the renal pelvis or ureter."
                ),
                Pathology(
                    name = "Acute Kidney Injury (AKI)",
                    presentation = "Sudden reduction in urine output (oliguria), rising serum creatinine and blood urea nitrogen (BUN), peripheral edema.",
                    pathologyDescription = "Arboreal tubular damage (such as ischemic or toxic Acute Tubular Necrosis) causing loss of glomerular filtration rate (GFR) and accumulation of nitrogenous waste."
                )
            )
        ),
        Organ(
            id = "kidney_right",
            name = "Right Kidney",
            latinName = "Ren Dexter",
            system = AnatomicalSystem.URINARY,
            position3D = Vector3D(0.20f, 0.42f, -0.14f),
            description = "The right renal filtering unit, positioned slightly lower than the left due to the mass of the overlying liver.",
            detailedStructure = listOf(
                "Cortex, Medulla, and Papillae directing urine downstream",
                "Glomerular capillary networks surrounded by Bowman's capsule space",
                "Surrounded by posterior retroperitoneal perirenal and pararenal fat pads",
                "Receives short right renal artery branching directly from the abdominal aorta"
            ),
            functions = listOf(
                "Helps maintain acid-base homeostasis via bicarbonate filtration and hydrogen ion secretion",
                "Performs vitamin D3 active conversion (hydroxylation) via 1-alpha-hydroxylase",
                "Regulates blood osmolarity by managing Aquaporin-2 water absorption channels"
            ),
            clinicalSignificance = listOf(
                "Renal Cell Carcinoma: Frequently presents with the classic triad of hematuria, flank pain, and a palpable abdominal mass.",
                "Pyelonephritis: Ascending urinary tract infection extending to the renal pelvis, causing fever, chills, and costovertebral angle (CVA) tenderness.",
                "Polycystic Kidney Disease (ADPKD): Genetic cystic enlargement bilaterally, eventually leading to end-stage renal disease."
            ),
            pathologies = listOf(
                Pathology(
                    name = "Acute Pyelonephritis",
                    presentation = "High fever, chills, flank pain, dysuria, urinary frequency, and positive costovertebral angle (CVA) tenderness.",
                    pathologyDescription = "Ascending enteric bacterial infection (often E. coli) from the lower urinary tract invading the renal pelvis and parenchymal interstitium, marked by neutrophilic infiltration."
                ),
                Pathology(
                    name = "Autosomal Dominant Polycystic Kidney Disease",
                    presentation = "Bilateral flank masses, hypertension, progressive renal failure, extrarenal aneurysms (such as berry aneurysms of Willis).",
                    pathologyDescription = "Mutations in PKD1 or PKD2 genes yielding progressive fluid-filled cyst expansion in nephrons that compresses and destroys adjacent healthy parenchyma."
                )
            )
        ),
        Organ(
            id = "pancreas",
            name = "Pancreas",
            latinName = "Pancreas",
            system = AnatomicalSystem.ENDOCRINE, // both digestive & endocrine
            position3D = Vector3D(-0.02f, 0.58f, 0.05f),
            description = "An elongated, retroperitoneal accessory digestive organ extending transversely from the C-loop of the duodenum to the hilum of the spleen.",
            detailedStructure = listOf(
                "Head, Neck, Body, and Tail regions",
                "Main Pancreatic Duct (Duct of Wirsung) merging with the common bile duct at the hepatopancreatic ampulla",
                "Exocrine Pancreatic Acini (cells producing pre-enzymes trypsinogen, lipase, amylase)",
                "Endocrine Islets of Langerhans (containing Alpha cells [glucagon], Beta cells [insulin], Delta cells [somatostatin])"
            ),
            functions = listOf(
                "Exocrine function: Secretes high-pH bicarbonate and digestive enzymes to neutralize acid chyme in the duodenum",
                "Endocrine function: Directs systemic blood glucose homeostasis via insulin, glucagon, and somatostatin hormonal release"
            ),
            clinicalSignificance = listOf(
                "Acute Pancreatitis: Autodigestion of pancreatic parenchyma due to premature trypsin activation. Correlated with alcohol abuse, gallstones, and high serum amylase/lipase.",
                "Pancreatic Carcinoma: Typically located in the pancreatic head, obstructing the common bile duct to produce painless, progressive jaundice (Courvoisier's Law).",
                "Diabetes Mellitus Type 1: Autoimmune destruction of pancreatic beta cells, causing profound insulin deficiency."
            ),
            pathologies = listOf(
                Pathology(
                    name = "Acute Pancreatitis",
                    presentation = "Severe, sharp epigastric abdominal pain radiating straight through to the back, relieved by leaning forward; accompanied by nausea, vomiting, and elevated serum lipase.",
                    pathologyDescription = "Premature activation of trypsinogen to trypsin inside pancreatic acinar cells, causing enzymatic autodigestion, interstitial edema, fat necrosis, and systemic inflammatory response syndrome (SIRS)."
                ),
                Pathology(
                    name = "Pancreatic Adenocarcinoma",
                    presentation = "Painless jaundice, weight loss, dark urine, pale stools, palpable non-tender gallbladder (Courvoisier's Sign), migratory thrombophlebitis (Trousseau's Syndrome).",
                    pathologyDescription = "Highly aggressive malignant tumor of ductal epithelium, typically arising in the head, leading to early local invasion and biliary obstruction."
                )
            )
        ),
        Organ(
            id = "gallbladder",
            name = "Gallbladder",
            latinName = "Vesica Biliaris",
            system = AnatomicalSystem.DIGESTIVE,
            position3D = Vector3D(0.16f, 0.62f, 0.16f),
            description = "A pear-shaped, muscular sac nestled in a shallow fossa on the visceral surface of the right hepatic lobe.",
            detailedStructure = listOf(
                "Fundus (projecting past the lower liver border), Body, and Neck regions",
                "Cystic Duct containing spiral valves of Heister to control flow",
                "Smooth muscle wall that contracts in response to Cholecystokinin (CCK) stimulus",
                "Highly folded mucosa without submucosal layer, optimizing salt and water absorption"
            ),
            functions = listOf(
                "Concentrates and stores hepatic bile (up to 10-fold by removing water and electrolytes)",
                "Ejects concentrated bile into the duodenum via cystic and common bile ducts for lipid digestion"
            ),
            clinicalSignificance = listOf(
                "Cholelithiasis: Cholesterol or bilirubin gallstones. Risk factors summarized as the '4 Fs': Female, Fat, Forty, Fertile.",
                "Murphy's Sign: Inspiratory arrest upon palpation of the right upper quadrant during deep inspiration; highly indicative of acute cholecystitis.",
                "Biliary Colic: Temporary impaction of a gallstone in the cystic duct, provoking self-limiting right upper quadrant pain after fatty meals."
            ),
            pathologies = listOf(
                Pathology(
                    name = "Acute Cholecystitis",
                    presentation = "Persistent right upper quadrant pain radiating to the right scapula, fever, leukocytosis, positive Murphy's sign, aggravated after fatty meals.",
                    pathologyDescription = "Impacted cystic duct gallstone triggers mechanical distention, chemical irritation, and secondary bacterial infection (typically E. coli) of the gallbladder wall."
                ),
                Pathology(
                    name = "Choledocholithiasis",
                    presentation = "Right upper quadrant pain, visible jaundice, dark urine, pale stools, elevated Alkaline Phosphatase (ALP) and direct bilirubin.",
                    pathologyDescription = "Migration of a gallstone from the gallbladder down into the common bile duct, causing mechanical biliary obstruction upstream."
                )
            )
        ),
        Organ(
            id = "intestine_small",
            name = "Small Intestine",
            latinName = "Intestinum Tenue",
            system = AnatomicalSystem.DIGESTIVE,
            position3D = Vector3D(0.0f, 0.28f, 0.13f),
            description = "An extensive, highly coiled segment of the alimentary canal linking the pylorus to the cecum, specialized for terminal enzymatic digestion and nutrient absorption.",
            detailedStructure = listOf(
                "Duodenum (mostly retroperitoneal C-loop wrapping the pancreas)",
                "Jejunum (superior left quadrant, containing dense, tall plicae circulares)",
                "Ileum (inferior right quadrant, characterized by Peyer's Patches and vitamin B12/bile acid transport)",
                "Highly adapted simple columnar mucosal lining with plicae circulares, villi, and microvilli"
            ),
            functions = listOf(
                "Completes chemical breakdown of carbohydrates, lipids, and proteins",
                "Absorbs vitamins, minerals, water, and metabolic monomers",
                "Synthesizes gastrointestinal hormones (Secretin, Cholecystokinin, GIP)"
            ),
            clinicalSignificance = listOf(
                "Celiac Disease: Autoimmune enteropathy triggered by gluten, yielding immune-mediated destruction of duodenal villi and malabsorption.",
                "Crohn's Disease: Chronic transmural inflammation that can affect any part of the GI tract, especially the distal terminal ileum.",
                "Small Bowel Obstruction (SBO): Mechanical blockage, frequently secondary to postoperative surgical bands of scar tissue (adhesions)."
            ),
            pathologies = listOf(
                Pathology(
                    name = "Celiac Sprue",
                    presentation = "Chronic diarrhea, steatorrhea, weight loss, fat-soluble vitamin deficiencies (A, D, E, K), iron-deficiency anemia.",
                    pathologyDescription = "IgA-mediated autoimmune reaction to gluten (specifically gliadin peptides) yielding mucosal inflammation, crypt hyperplasia, and progressive duodenal villous atrophy."
                ),
                Pathology(
                    name = "Crohn's Disease",
                    presentation = "Crampy lower right quadrant abdominal pain, diarrhea (usually non-bloody), apthous ulcers, weight loss, fistulas.",
                    pathologyDescription = "Chronic transmural autoimmune inflammation characterized by patchy 'skip lesions' mostly in the terminal ileum, non-caseating granulomas, and cobblestone mucosal appearance."
                )
            )
        ),
        Organ(
            id = "colon",
            name = "Large Intestine (Colon)",
            latinName = "Colon",
            system = AnatomicalSystem.DIGESTIVE,
            position3D = Vector3D(0.0f, 0.15f, 0.12f),
            description = "The terminal muscular conduit of the gastrointestinal system, extending from the ileocecal valve to the anal canal, running a frame-like course in the abdomen.",
            detailedStructure = listOf(
                "Cecum and Appendix (vermiform appendix, a blind pouch in the lower right quadrant)",
                "Ascending, Transverse, Descending, and Sigmoid segments",
                "Rectum and Anal Canal",
                "Distinct gross features: Taeniae Coli (muscular bands), Haustra (pouchings), and Epiploic Appendages (fat-filled pouches)"
            ),
            functions = listOf(
                "Absorbs residual water, sodium, and potassium from liquid fecal digest",
                "Stores and compacts fecal waste until defecation",
                "Hosts dense symbiotic gut microbiota synthesizing Vitamin K and biotin"
            ),
            clinicalSignificance = listOf(
                "Appendicitis: Acute inflammation of the appendix, classically presenting with pain radiating from the periumbilical region to McBurney's Point.",
                "Diverticulitis: Microperforation of herniated colonic mucosa (diverticula) presenting as painful acute left-lower quadrant pain.",
                "Colorectal Adenocarcinoma: Left-sided lesions typically present with obstruction and modern 'pencil-thin' stools, right-sided with occult bleeding."
            ),
            pathologies = listOf(
                Pathology(
                    name = "Acute Appendicitis",
                    presentation = "Periumbilical abdominal pain migrating to the lower right quadrant (McBurney's point), accompanied by low-grade fever, nausea, rebound tenderness, and Rovsing's sign.",
                    pathologyDescription = "Lumen obstruction (frequently due to a fecalith or lymphoid hyperplasia) leads to mucus accumulation, increased intraluminal pressure, ischemia, and subsequent bacterial invasion."
                ),
                Pathology(
                    name = "Ulcerative Colitis (UC)",
                    presentation = "Chronic, bloody diarrhea accompanied by lower left quadrant crampy abdominal pain, tenesmus, and extraintestinal symptoms (such as uveitis).",
                    pathologyDescription = "Chronic, mucosal-only inflammatory disease starting in the rectum and extending continuously proximally, characterized by crypt abscesses, mucosal ulceration, and pseudopolyps."
                )
            )
        ),
        Organ(
            id = "bladder",
            name = "Urinary Bladder",
            latinName = "Vesica Urinaria",
            system = AnatomicalSystem.URINARY,
            position3D = Vector3D(0.0f, -0.15f, 0.13f),
            description = "A highly distensible, hollow muscular sac situated in the lesser pelvis posterior to the pubic symphysis, acting as a dynamic reservoir for urine.",
            detailedStructure = listOf(
                "Composed of the robust Detrusor Muscle (three interlaced smooth muscle layers)",
                "The Trigone: Smooth, triangular base segment outlined by the two ureteric orifices and the urethral exit",
                "Lined by highly expandable Transitional Epithelium (Urothelium)",
                "Controlled by the involuntary Internal Urethral Sphincter and voluntary External Sphincter"
            ),
            functions = listOf(
                "Stores excreted urine under low pressure, expanding comfortably up to ~500mL",
                "Contracts (Micturition Reflex) mediated by parasympathetic fibers to evacuate urine"
            ),
            clinicalSignificance = listOf(
                "Cystitis: Infection of the urinary bladder, classical symptoms are dysuria, frequency, urgency, and suprapubic pain.",
                "Urinary Retention: Acute inability to pass urine, often secondary to benign prostatic hyperplasia (BPH) in males.",
                "Transitional Cell Carcinoma: Primary bladder malignancy highly linked to tobacco smoke and industrial dye exposure, presenting as painless macroscopic hematuria."
            ),
            pathologies = listOf(
                Pathology(
                    name = "Acute Cystitis",
                    presentation = "Dysuria, urinary urgency, increased frequency, suprapubic tenderness, cloudy or foul-smelling urine.",
                    pathologyDescription = "Ascending bacterial infection (predominantly fecal flora like Escherichia coli) colonizes and inflames the bladder urothelial mucosa."
                ),
                Pathology(
                    name = "Bladder Outlet Obstruction",
                    presentation = "Hesitancy, weak urinary stream, sensation of incomplete voiding, nocturia; if acute, intolerable suprapubic distention and pain.",
                    pathologyDescription = "Physical mechanical blockade at the bladder neck, most commonly triggered by progressive Benign Prostatic Hyperplasia (BPH) or urethral strictures, yielding detrusor hypertrophy."
                )
            )
        )
    )

    fun getOrganById(id: String): Organ? = organs.find { it.id == id }
    fun getOrgansBySystem(system: AnatomicalSystem): List<Organ> = organs.filter { it.system == system }
}
