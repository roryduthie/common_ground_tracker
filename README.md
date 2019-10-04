# common_ground_tracker:

This repository contains a program that anlayzes files that are annotated with information based on Inference Anchoring Theory (Budzynska et al. 2014). 
Based on a set of rules the system translates the IAT annotation into a discourse model based on Gunlogson (2002,2004).  

The system provides a visual representation of the analysis containing a list of propositions that contribute to discourse model
as well as the relation between propositions and speakers. The system uses color coding to express various states.


# Running the system

Simply running the far file opens a GUI. Press the "load IATmap" button and select a directory containing IAT files in .json format. 
It is also possible to call the jar with the parameter "-i sample/directory" do circumvent this step. 

# Using the system
The system provides a list of propositions on the left. When a set of .sjon files (i.e. a directory) has been loaded
there should be one proposition in the it (the first of the proposition of the IAT file). By pressing on the "<<" and ">>" buttons 
you can browse through the debate. 

The upper right window displays the discourse participants of the current debate. Clicking on a proposition highlights the discourse 
participants who share that belief. Additional states can be accessed via the buttons in the lower right:

Show commitments:
- Highlights in green all the propositions that are shared commitments of ALL selected discourse participants
- Highlights in blue propositions that are commitments held by some selected discourse participant

Show unresolved: 
- Highlights in orange propositions that are commitments held by some discourse participant and that are not rejected by any other discourse
participant

Show controversial:
- Highlights in red propositions that are shared by some discourse participants but rejected by others. 


# References:

Budzynska, Katarzyna, et al. "Towards Argument Mining from Dialogue." COMMA. 2014.

Gunlogson, Christine. "Declarative questions." Semantics and Linguistic Theory. Vol. 12. 2002.

Gunlogson, Christine. True to form: Rising and falling declaratives as questions in English. Routledge, 2004.
