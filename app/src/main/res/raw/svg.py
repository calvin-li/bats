from os import system
svg = r'<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg version="1.1" id="Layer_1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" width="WIDE" height="50px" viewBox="0 0 100 100" xml:space="preserve">   <text      x="50" y="80" fill="white" stroke="COLOR" stroke-width="5px" font-size="85px" text-anchor="middle" font-weight="bold" font-family="monospace">PERCENT</text></svg>'
for i in range(0,101):
	color = "black"
	if i < 20:
		color = "red"
	elif i < 40:
		color = "orange"
	elif i < 60:
		color = "yellow"
	elif i < 80:
		color = "limegreen"
	else:
		color = "forestgreen"
	width = "50px"
	if i == 100:
		width = "75px"
	out = svg.replace("COLOR", color)
	out = out.replace("PERCENT",str(i))
	out = out.replace("WIDE", width)
	file = "percent/" + str(i) + ".svg"
	with open(file, "w") as f:
		f.write(out)
	f.closed
	png = "../mipmap-hdpi/a" + str(i) + ".png"
	system("start /b inkscape -f " + file + " -e " + png)