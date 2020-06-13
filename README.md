# CMSC423 (Bioinformatic Algorithms, Databases, and Tools) Project 1 (Variant Detection)

The goal of this project was to build a read aligner with the purpose of discovering variants in the Coronavirus genome. 

*The problem of variant finding* <br />
The problem of using a sequenced sample to determine genomic variants with respect to some reference genome is common. 
One of the big challenges includes potentially low sequencing coverage of the variant regions, which makes it difficult to distinguish a true
variant from sequencing errors.

The task required a tool to align reads to the reference genome. Then, by looking at "pile ups" of the reads on the reference genome, it was 
easy to determine places where the sequenced sample differes from the reference data, as evidenced by the repeated variation in the reads that cover a
certain position in the genome

*The Data Provided*<br />
I was given the reference genome of a particular strain of the coronavirus (2019-nCoV/USA-lL 1/2020). It was provided as a single
"contig".

The single-end sequencing reads were also provided as a gzip file to save space. 

*The Goal*<br />
To write a read alignment tool, based on the seed-and-extend or seed-and-vote paradigm using the FM-Index for efficient seed finding. After this 
the alignments of the sequencing reads was accomplished. This was done by taking the reads and turning them into a SAM format for easy processing
by IGV (Integrative Genomics Viewer).
