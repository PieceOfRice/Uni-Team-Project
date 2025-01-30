# Introduction
I was responsible for implementing the tournament view page - the specific page responsible for displaying all of the data and information about a given tournament, alongside its current live bracket status.
The page also allows registered users to sign up their teams to it.

# Frontend
The frontend is responsive to all screen dimensions.
The brackets for the on-going matches are generated dynamically from the existing match data for a given tournament, and are hence reactive to realtime updates.
Although expensive to compute, it is always accurate.

# Backend
Due to the nature of the feature, the backend mostly just focuses on data retrieval.
That being said, when signing people up, it creates new participation data linked to the tournament.
If the tournament hasn't begun, it also creates the necessary match objects for the first brackets of matches, in order for the user interface to correctly display them.
The rest of the match generation and updating of match data is handled by Rhys' match pages.