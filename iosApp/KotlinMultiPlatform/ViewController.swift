//
//  ViewController.swift
//  KotlinMultiPlatform
//
//  Created by Rohit Singh on 15/6/20.
//  Copyright © 2020 Rohit Singh. All rights reserved.
//

import UIKit
import SharedCode
class ViewController: UIViewController , UITableViewDelegate,  UITableViewDataSource{
        let viewModel = NewsListViewModel.Companion.init().create()
        
      var newsList = [NewsArticles]()
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.newsList.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath)
        let news = newsList[indexPath.row]
        cell.textLabel?.text = news.title
        cell.detailTextLabel?.text = news.description()
               return cell
    }


    @IBOutlet var tableView: UITableView!
    @IBOutlet var indicator: UIActivityIndicatorView!
    override func viewDidLoad() {
        super.viewDidLoad()

       tableView.delegate = self
    tableView.dataSource = self

     viewModel.observeState().watch {
        uiState in
        self.showNews(state: uiState)
    }

 }

    func showNews(state: NewsListState?) {
        guard let stateValue = state else {return}
       if(stateValue.newsList != nil) {
        if (stateValue.loading) {
            indicator.isHidden = false
        } else {
              indicator.isHidden = true
        }

        self.newsList.append(contentsOf: stateValue.newsList?.articles ?? [])
          self.tableView.reloadData()
 }
}
    
    deinit {
        viewModel.onCleared()
    }


}


