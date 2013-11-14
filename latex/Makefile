TEXFILES := $(wildcard *.tex)

all: Pflichtenheft.pdf
	

Pflichtenheft.pdf:  $(TEXFILES)
	pdflatex Pflichtenheft
	bibtex Pflichtenheft
	pdflatex Pflichtenheft
	pdflatex Pflichtenheft


clean:
	rm *.aux *.bbl *.blg *.log *.toc Pflichtenheft.pdf


