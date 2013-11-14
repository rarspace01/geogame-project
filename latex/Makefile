TEXFILES := $(wildcard *.tex)

all: Masterarbeit.pdf
	

Masterarbeit.pdf:  $(TEXFILES)
	pdflatex Masterarbeit
	bibtex Masterarbeit
	pdflatex Masterarbeit
	pdflatex Masterarbeit


clean:
	rm *.aux *.bbl *.blg *.log *.toc Masterarbeit.pdf


