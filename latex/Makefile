TEXFILES := $(wildcard *.tex)

all: Masterarbeit.pdf
	

Masterarbeit.pdf:  $(TEXFILES)
	pdflatex Masterarbeit
	biber Masterarbeit
	pdflatex Masterarbeit
	pdflatex Masterarbeit


clean:
	rm *.aux *.bbl *.blg *.log *.toc Masterarbeit.pdf
	del *.aux *.bbl *.blg *.log *.toc Masterarbeit.pdf


