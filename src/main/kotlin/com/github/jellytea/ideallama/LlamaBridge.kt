// Copyright (C) 2024 JetERA Creative
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.

package com.github.jellytea.ideallama

import org.apache.http.client.utils.URIBuilder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.CompletableFuture

class LlamaBridge(val addr: String) {
    fun generate(model: String, prompt: String): CompletableFuture<HttpResponse<String>> {
        val req = HttpRequest.newBuilder().uri(
            URIBuilder()
                .setScheme("http")
                .setHost(addr)
                .setPath("/generate")
                .setParameter("model", model)
                .setParameter("prompt", prompt)
                .build().toURL().toURI()
        ).build()

        return HttpClient.newHttpClient().sendAsync(req, HttpResponse.BodyHandlers.ofString())
    }
}
