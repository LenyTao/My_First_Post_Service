package com.PostService

import java.util.Date

case class Event(externalClientId: String, stage: String, status: Boolean, data: Date)
