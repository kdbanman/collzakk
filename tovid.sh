#!/bin/bash

./ffmpeg -r 30 -i screens/frame%06d.png -pix_fmt yuv420p -vcodec libx264 -crf 15 -s 900x900 -b:v 8000k out.mp4