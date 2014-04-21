TEXFILES := $(wildcard *.tex)

all: Masterarbeit.pdf showpdf

Masterarbeit.pdf:  $(TEXFILES)
	pdflatex Masterarbeit
	biber Masterarbeit
	pdflatex Masterarbeit
	pdflatex Masterarbeit

printpdf:
	gs -o cmyk.pdf -sDEVICE=pdfwrite -sColorConversionStrategy=CMYK -sProcessColorModel=DeviceCMYK -sOutputICCProfile=local.icc -sColorImageDownsampleThreshold=2 -sColorImageDownsampleType=Bicubic -sColorImageResolution=300 -sGrayImageDownsampleThreshold=2 -sGrayImageDownsampleType=Bicubic -sGrayImageResolution=300 -sMonoImageDownsampleThreshold=2 -sMonoImageDownsampleType=Bicubic -sMonoImageResolution=1200 -dSubsetFonts=true -dEmbedAllFonts=true -sCannotEmbedFontPolicy=Error -c ".setpdfwrite<</NeverEmbed[ ]>> setdistillerparams" -f Masterarbeit.pdf 


showpdf:
	gnome-open Masterarbeit.pdf

clean:
	rm *.aux *.bbl *.blg *.log *.toc *.out *.lot *.lof *.bcf *.4ct *.4tc Masterarbeit.pdf
	del *.aux *.bbl *.blg *.log *.toc *.out *.lot *.lof *.bcf *.4ct *.4tc Masterarbeit.pdf


