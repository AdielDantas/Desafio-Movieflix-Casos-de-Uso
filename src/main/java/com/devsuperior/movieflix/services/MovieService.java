package com.devsuperior.movieflix.services;

import com.devsuperior.movieflix.dto.MovieCardDTO;
import com.devsuperior.movieflix.dto.MovieDetailsDTO;
import com.devsuperior.movieflix.entities.Movie;
import com.devsuperior.movieflix.repositories.MovieRepository;
import com.devsuperior.movieflix.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovieService {

    @Autowired
    private MovieRepository repository;

    @Transactional(readOnly = true)
    public MovieDetailsDTO findById(Long id) {
        Movie movie = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não localizado"));
        return new MovieDetailsDTO(movie);
    }

    @Transactional(readOnly = true)
    public Page<MovieCardDTO> findAllPaged(Long genreId, Pageable pageable) {

        boolean hasCustomSorting = pageable.getSort().isSorted();

        Pageable sortedPageable;
        if (hasCustomSorting) {
            sortedPageable = pageable;
        } else {
            sortedPageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by("title").ascending()
            );
        }

        Page<Movie> moviePage;
        if (genreId == null || genreId == 0) {
            moviePage = repository.findAll(sortedPageable);
        } else {
            moviePage = repository.findByGenre(genreId, sortedPageable);
        }

        return moviePage.map(movie -> new MovieCardDTO(movie));
    }
}
