package com.plasmaconduit.jabba.webschemas

import com.plasmaconduit.jabba._


sealed trait TwitterCard

case class SummaryTwitterCard(url: URL,
                              title: String,
                              description: String,
                              image: Option[URL]) extends TwitterCard

case class LargeSummaryTwitterCard(url: URL,
                                   title: String,
                                   description: String,
                                   image: Option[URL]) extends TwitterCard

case class PhotoTwitterCard(url: URL,
                            title: Option[String],
                            image: URL,
                            width: Option[Int],
                            height: Option[Int]) extends TwitterCard

case class GalleryTwitterCard(url: URL,
                              title: Option[String],
                              description: Option[String],
                              image0: URL,
                              image1: URL,
                              image2: URL,
                              image3: URL) extends TwitterCard

